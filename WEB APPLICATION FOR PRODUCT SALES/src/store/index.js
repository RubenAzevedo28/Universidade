import Vue from 'vue'
import Vuex from 'vuex'
import products from './products'
import categories from './categories'
import user from './user'
import orders from './orders'
import basket from './basket'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {    
    products,
    user,
    orders,
    basket,
    categories  
  }
})
