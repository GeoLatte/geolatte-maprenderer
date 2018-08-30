import { Component } from '@angular/core'
import { Router } from '@angular/router'
import '../style/app.scss'
import { isNullOrUndefined } from 'util'
import 'rxjs/Rx'


@Component({
  selector: 'web-app',
  providers: [],
  template: `<example></example>`
})

export class AppComponent {

  constructor(private router: Router) {


  }

  navigate(route: string) {
    this.router.navigate([route])
  }

}
