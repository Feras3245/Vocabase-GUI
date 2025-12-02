import './assets/styles/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
// import type { Niveaus } from './types'

const init = async () => {
    // const response = await fetch('/notion')
    // const data:Niveaus = await response.json()
    // const app = createApp(App, {niveaus:data})
    const app = createApp(App)
    app.use(createPinia())
    app.mount('#app')
}

init()


