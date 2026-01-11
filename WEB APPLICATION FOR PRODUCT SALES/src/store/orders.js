  const orders = {
  namespaced: true,
  state: {
    orders: 
    [
    //{
    //"id":"11",
    //"customer_id":"20",
    //"created_at":"2021-12-03 18:20:31",
    //"status_id":"1",
    //"total":"190",
    //"order_items":[{"product_id":"2","name":"Salmon Roll","price":"18","quantity":"4"},{"product_id":"3","quantity":"3"},{"product_id":"4","quantity":"2"}]
    //}
    ]
  },
  getters: {
    getOrders (state) {
      return state.orders
    },   
  }, 
  mutations: {
    addOrders(state, orders){
        state.orders = orders
    },
    addOrder(state, order){
      state.orders = [...state.orders, order]
  },

	},
  actions: {
    async getMyOrdersFromDB({commit, rootGetters}) {
			try {
				const response = await fetch(`http://daw.deei.fct.ualg.pt/~a68005/LAB11/api/orders.php?session_id=${rootGetters['user/getUser'].session_id}`)
				const data = await response.json()
        commit('addOrders', data)
			} 
			catch (error) {
        console.log('error: ', error)
			}
		},
    async addOrder({commit, rootGetters}, order) {
			try {
				const response = await fetch(`http://daw.deei.fct.ualg.pt/~a68005/LAB11/api/orders.php?session_id=${rootGetters['user/getUser'].session_id}`, {
					method: 'POST',
					body: JSON.stringify(order),
					headers: { 'Content-type': 'application/json; charset=UTF-8' },
				})
				const data = await response.json()
          console.log('received data:',data)
          commit('addOrder', data)
          return true
			} 
			catch (error) {
				console.error(error)
        return false
			}
		},
  },
  modules: {
  }
}
export default 
	orders