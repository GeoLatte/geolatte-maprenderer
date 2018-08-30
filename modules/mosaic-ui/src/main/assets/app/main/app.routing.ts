import { RouterModule, Routes } from '@angular/router'
import { ExampleComponent } from '../example/example.component'

const routes: Routes = [
  {
    path: '',
    redirectTo: 'view/hello',
    pathMatch: 'full'
  },
  {
    path: 'view/hello',
    component: ExampleComponent
  }
]

export const routing = RouterModule.forRoot(routes)
