 
  const categories = {
  namespaced: true,
  state: {
    categories: [
    // {
    //"id":"1",
    //"name":"Pizza",
    //"description":"NULL",
    //"image":"NULL",
    //}
    ]
  },
  getters: {
    getCategories (state) {
      return state.categories;
    },   
  }, 
  mutations: {
    addCategories(state, categories){
        state.categories = categories
    },

    },
  actions: {
    async getCategoriesFromDB({commit}) {
            try {
                const response = await fetch('http://daw.deei.fct.ualg.pt/~a68005/LAB11/api/categories.php')
                const data = await response.json()
                console.log('received data:', data)                
                commit('addCategories', data)
                return true
            } 
            catch (error) {
                console.log('error: ', error)
                return false
            }
        },
  },
  modules: {
  }
}
export default 
    categories
  