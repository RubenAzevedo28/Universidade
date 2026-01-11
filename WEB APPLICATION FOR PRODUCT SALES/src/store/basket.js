 
  const basket = {
  namespaced: true,
  state: {
    products: [
    //  {
    //    id: 1,
    //    quantity: 5
    //  },
    //  {
    //   id: 2,        
    //    quantity: 2
    //  },
    //  {
    //    id: 3,        
    //    quantity: 3
    //  }
    ]
  },
  getters: {
    getProducts (state) {
      return state.products;
    },   
  }, 
  mutations: {
    clearBasket (state) {
        state.products = []
    },
    incrementProduct (state, idToIncrement) {
        // check if item exists in basket
        let itemExists = state.products.some(function(product) {
            return product.id == idToIncrement;
        })

        // item does not exist; create item
        if (!itemExists) {
            state.products.push({
                id: idToIncrement,
                quantity: 0
            })
        }
        // increment item
        state.products.forEach( function (product) {     
            if (product.id == idToIncrement)  
                product.quantity++
        })
      },    
      decrementProduct (state, idToDecrement) {
        state.products.forEach( function (product, index) {       
            if (product.id == idToDecrement && product.quantity >= 1)        
                product.quantity--
            // remove item if quantity is 0
            if (product.quantity == 0)
                state.products.splice(index,1)
        })
      },
	},
  actions: {
    increment({commit}, id) {
      commit('incrementProduct', id)
      return true
    }
  },
  modules: {
  }
}
export default 
	basket