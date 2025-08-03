import { createApp } from 'vue'
import { vividVue } from '@vonage/vivid-vue'
import { createPinia } from 'pinia'
import router from './router'
import App from './App.vue'
import 'leaflet/dist/leaflet.css';


const app = createApp(App)
    .use(vividVue, {
		font: 'spezia',
		//customComponentPrefix: 'my-app',
    })

app.use(createPinia())
app.use(router)

app.mount('#app')