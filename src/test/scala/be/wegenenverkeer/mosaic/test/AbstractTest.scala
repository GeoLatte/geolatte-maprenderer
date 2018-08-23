package be.wegenenverkeer.mosaic.test

import _root_.slick.jdbc.JdbcBackend
import _root_.slick.jdbc.JdbcBackend.DatabaseDef
import be.wegenenverkeer.atomium.format.Url
import be.wegenenverkeer.mosaic.api.AppPoAuth
import be.wegenenverkeer.mosaic.infrastructure.SlickPgProfile.api._
import be.wegenenverkeer.mosaic.infrastructure._
import be.wegenenverkeer.slick3.DbRunner
import be.wegenenverkeer.poauth.infrastructure.MockPoProvider
import be.wegenenverkeer.restfailure.{RestException, RestFailure}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FunSuite, Matchers}
import play.api.db.slick.SlickComponents
import play.api.mvc.EssentialFilter
import play.api.routing.Router
import play.api.{ApplicationLoader, BuiltInComponentsFromContext, Environment}
import resource._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.control.NonFatal


/**
  * Abstract integration test is geschikt om tests uit te voeren die echte repos gebruiken.
  * Alles wat binnen 1 test method gebeurt heeft zijn eigen lege databank (let op: NIET zijn eigen transactie)
  *
  */
abstract class AbstractTest
  extends FunSuite
    with BeforeAndAfterEach with BeforeAndAfterAll
    with Matchers {

  var deps: TestComponents = _

  private def recreateDb(): Unit = {

    val db: DatabaseDef = Database.forConfig("slick.dbs.testdbsetup").asInstanceOf[JdbcBackend.DatabaseDef]

    dropCreatePublicSchema(db)
//    createDomainTables(db)
//    createFeedTables(db)
//    createAkkaTables(db)
    db.close()

  }

  def dropCreatePublicSchema(db: DatabaseDef): Unit = {

    managed(db.createSession()) acquireAndGet { session =>
      session.prepareStatement(
        """drop schema public cascade;
           create schema public;
           create extension hstore;"""
      ).executeUpdate()
    }

  }

  def createDomainTables(db: DatabaseDef): Unit = {

//    val dbRunner = new DbRunner(db)
//    val gebruikerViewRepo = new GebruikerViewRepo(dbRunner)
//    val dbios = for {
//      gebruikerSchemaDbIO <- gebruikerViewRepo.tableQuery.schema.create
//    } yield (gebruikerSchemaDbIO)
//
//    db.run(dbios).block
  }

  def createFeedTables(db: DatabaseDef): Unit = {
    val sql =
      """
        |CREATE TABLE feed_entries_gebruiker
        |(
        |  id serial NOT NULL,
        |  uuid character varying,
        |  value text,
        |  "timestamp" timestamp without time zone NOT NULL,
        |  CONSTRAINT feed_entries_gebruiker_pkey PRIMARY KEY (id)
        |);
      """.stripMargin

    managed(db.createSession()) acquireAndGet { session =>
      session.prepareStatement(sql).executeUpdate()
    }

  }

  def createAkkaTables(db: DatabaseDef): Unit = {
     val rowIdSeq =
        """
          |
          |CREATE TABLE JOURNAL (
          |  id BIGSERIAL NOT NULL PRIMARY KEY,
          |  persistenceid VARCHAR(254) NOT NULL,
          |  sequencenr INT NOT NULL,
          |  rowid BIGINT DEFAULT NULL,
          |  partitionkey VARCHAR(254) DEFAULT NULL,
          |  deleted BOOLEAN DEFAULT false,
          |  sender VARCHAR(512),
          |  payload BYTEA,
          |  payloadmf VARCHAR(512),
          |  manifest VARCHAR(512),
          |  uuid VARCHAR(254) NOT NULL,
          |  writeruuid VARCHAR(254) NOT NULL,
          |  created timestamptz NOT NULL,
          |  tags HSTORE,
          |  event JSONB,
          |  constraint cc_journal_payload_event check (payload IS NOT NULL OR event IS NOT NULL));
          |
          |CREATE UNIQUE INDEX journal_pidseq_idx ON JOURNAL (persistenceid, sequencenr);
          |CREATE INDEX journal_event_idx ON JOURNAL USING gin (event);
          |CREATE INDEX journal_rowid_idx ON JOURNAL (rowid);
          |CREATE SEQUENCE journal_rowid_seq;
          |
          |CREATE VIEW EVENTS AS
          |  SELECT
          |    id,
          |    persistenceid,
          |    sequencenr,
          |    uuid,
          |    created,
          |    tags,
          |    payloadmf,
          |    event
          |  FROM JOURNAL WHERE event is NOT NULL;
          |
          |CREATE TABLE SNAPSHOT (
          |  persistenceid VARCHAR(254) NOT NULL,
          |  sequencenr INT NOT NULL,
          |  partitionkey VARCHAR(254) DEFAULT NULL,
          |  timestamp BIGINT NOT NULL,
          |  snapshot BYTEA,
          |  PRIMARY KEY (persistenceid, sequencenr)
          |);
          |
        """.stripMargin
        managed(db.createSession()) acquireAndGet { session =>
          session.prepareStatement(rowIdSeq).executeUpdate()
        }

  }

  // Het refreshen van de DB is voor deze testen niet zo triviaal omdat het aanmaken van de DB de runtime componenten
  // vereist. Echter, het actorsysteem moeten we afzetten voor het refreshen van de DB omdat we anders exceptions krijgen
  // tijdens het uitvoeren van de testen. Op zich zijn deze exceptions niet erg omdat ze gebeuren bij de opzet van de
  // testen, zonder dat het impact heeft op de testen zelf. Maar het ziet er niet mooi uit.
  override protected def beforeAll(): Unit = {
    super.beforeAll()
  }

  override protected def afterEach(): Unit = {
    super.afterEach()
    // We stoppen eerst het actorsysteem, zoniet krijgen we fouten bij het hercreëren van de DB.
    shutdownActorSystem()
    deps = null
  }


  private def shutdownActorSystem() : Unit = {
    implicit val executionContext: ExecutionContext = deps.actorSystem.dispatcher
    val actorSystemShutdown =
      for {
        _ <- deps.actorSystem.terminate()
        _ <- deps.actorSystem.whenTerminated
      } yield ()

    Await.ready(actorSystemShutdown, 5.seconds)
  }

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    recreateDb()
    deps = new TestComponents
  }

  class TestComponents
    extends BuiltInComponentsFromContext(ApplicationLoader.createContext(Environment.simple()))
      with AppModule
      with SlickComponents {

    def router: Router = Router.empty

    override lazy val httpFilters = List.empty[EssentialFilter]

    lazy val db: DatabaseDef = Database.forConfig("slick.dbs.testdbsetup").asInstanceOf[JdbcBackend.DatabaseDef]

    val dbRunner = new DbRunner(db, SlickPgProfile)

    lazy val appPoAuth: AppPoAuth = new AppPoAuth(new MockPoProvider(Map.empty))

    lazy val feedBaseUrl: Url = new Url("http://local.awv/gebruikersdb")
  }

  implicit class FutureHttpOps[T](val future: Future[T]) {

    /**
      * Blokkeer op het result van een Future
      */
    def block(implicit exc: ExecutionContext): T = {
      blockFor(10.seconds)
    }

    /**
      * Blokkeer op het result van een Future voor gegeven duration
      */
    def blockFor(duration: Duration)(implicit exc: ExecutionContext): T = {
      Await.result(future, duration)
    }


    /**
      * Blokkeer op de Future en verwacht dat de Future faalt met een RestException
      * @param onFailure krijgt de RestFailure die in de RestException zat
      */
    def expectRestFailure(onFailure: RestFailure => Unit)(implicit exc: ExecutionContext): Unit = {
      try {
        val res = block
        sys.error(s"De future moest falen maar was een success met als resultaat $res")
      }
      catch {
        case RestException(restfailure) => onFailure(restfailure)
        case NonFatal(e)                =>
          e.printStackTrace()
          sys.error(s"We verwachtten een RestException maar is gefaald met een $e")
      }
    }
  }

}
