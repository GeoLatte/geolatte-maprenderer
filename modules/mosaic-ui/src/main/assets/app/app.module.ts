import { NgModule, ApplicationRef, NO_ERRORS_SCHEMA, LOCALE_ID } from '@angular/core'
import { BrowserModule } from '@angular/platform-browser'
import { HttpModule, JsonpModule } from '@angular/http'
import { FormsModule, ReactiveFormsModule } from '@angular/forms'
import {
  MatDialogModule, MatButtonModule, MatSelectModule, MatListModule, MatCardModule, MatInputModule, MatTabsModule, MatToolbarModule,
  MatAutocompleteModule, MatProgressBarModule
} from '@angular/material'

import { removeNgStyles, createNewHosts } from '@angularclass/hmr'

import { Observable } from 'rxjs/Observable'

// -- App components
import { ExampleComponent } from './example/example.component'
import { ExampleService } from './example/example.service'
// -- Main
import { AppComponent } from './app.component'
import { routing } from './main/app.routing'

import { BrowserAnimationsModule } from '@angular/platform-browser/animations'
import { trigger, state, style, transition, animate } from '@angular/animations'
import { AnimationDriver } from '@angular/animations/browser'
// -- Remote-validatie
// import {
//   RemoteValidationErrorsDirective,
//   InputContainerRemoteValidationErrorsDirective,
//   FormGroupRemoteValidationErrorsDirective,
//   FormRemoteValidationErrorsDirective
// } from './remote-validation/remote-validation.directive'




@NgModule(
  {

    imports: [
      BrowserModule,
      BrowserAnimationsModule,
      HttpModule,
      JsonpModule,
      FormsModule,
      ReactiveFormsModule,
      MatButtonModule, // material modules: https://github.com/angular/material2/blob/master/src/lib/module.ts
      MatDialogModule,
      MatSelectModule,
      MatListModule,
      MatCardModule,
      MatInputModule,
      MatTabsModule,
      MatToolbarModule,
      MatProgressBarModule,
      MatAutocompleteModule,
      routing
    ],

    declarations: [
      AppComponent,
      ExampleComponent
      // ,
      // RemoteValidationErrorsDirective,
      // InputContainerRemoteValidationErrorsDirective,
      // FormGroupRemoteValidationErrorsDirective,
      // FormRemoteValidationErrorsDirective
    ],

    // entryComponents: [MainTabsComponent],

    providers: [
      { provide: LOCALE_ID, useValue: 'nl-BE' },
      ExampleService
    ],

    bootstrap: [AppComponent],

    schemas: [NO_ERRORS_SCHEMA] // ensures that we don't get errors on unkown tags
    // We need this in the transclusion of some components like collapsible.
  }
)
export class AppModule {

  constructor(public appRef: ApplicationRef) {
  }

  hmrOnInit(store) {
    console.log('HMR store', store)
  }

  hmrOnDestroy(store) {
    let cmpLocation = this.appRef.components.map(cmp => cmp.location.nativeElement)
    // recreate elements
    store.disposeOldHosts = createNewHosts(cmpLocation)
    // remove styles
    removeNgStyles()
  }

  hmrAfterDestroy(store) {
    // display new elements
    store.disposeOldHosts()
    delete store.disposeOldHosts
  }
}
