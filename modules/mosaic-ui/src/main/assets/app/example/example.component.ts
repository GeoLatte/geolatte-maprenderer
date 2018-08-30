import { Component } from '@angular/core'
import { Router } from '@angular/router'
import { isNullOrUndefined } from 'util'
import 'rxjs/Rx'
import {ExampleService} from './example.service'


@Component({
  selector: 'example',
  providers: [],
  template: `<h1>Hello World!</h1>`
})

export class ExampleComponent {

  constructor(private exampleService: ExampleService, private router: Router) {
    exampleService.doSomething('bar')
  }

  navigate(route: string) {
    this.router.navigate([route])
  }

}
