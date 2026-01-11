<template>
<div id="app">
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark p-2 fixed-top">
		<div class="container-fluid">
			<a class="navbar-brand" href="#">G Spot</a>
			<button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNavDropdown" aria-controls="navbarNavDropdown" aria-expanded="true" aria-label="Toggle navigation">
				<span class="navbar-toggler-icon"></span>
			</button>
			<div class=" navbar-collapse justify-content-end collapse" id="navbarNavDropdown">
				<ul class="navbar-nav me-auto mb-2 mb-lg-0">
					<li class="nav-item">
						<router-link class="nav-link active" aria-current="page" to='/'><span class="glyphicon glyphicon-home"></span>{{MENU_1}}</router-link>
					</li>
				</ul>
				<ul v-if="!userLoggedIn" class="navbar-nav">
					<li class="nav-item">
						<router-link class="nav-link mx-2" to='/basket'>
							<svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="currentColor" class="bi bi-cart " viewBox="0 0 16 16"> <path d="M0 1.5A.5.5 0 0 1 .5 1H2a.5.5 0 0 1 .485.379L2.89 3H14.5a.5.5 0 0 1 .491.592l-1.5 8A.5.5 0 0 1 13 12H4a.5.5 0 0 1-.491-.408L2.01 3.607 1.61 2H.5a.5.5 0 0 1-.5-.5zM3.102 4l1.313 7h8.17l1.313-7H3.102zM5 12a2 2 0 1 0 0 4 2 2 0 0 0 0-4zm7 0a2 2 0 1 0 0 4 2 2 0 0 0 0-4zm-7 1a1 1 0 1 1 0 2 1 1 0 0 1 0-2zm7 0a1 1 0 1 1 0 2 1 1 0 0 1 0-2z"/></svg>
							<span class="badge bg-dark">{{totalproduct}}</span>
						</router-link>
					</li>
					<li class="nav-item dropdown">
						<a class="nav-link dropdown-toggle" href="{$LINK_3}" id="navbarDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false">{{MENU_5}}</a>
						<ul class="dropdown-menu dropdown-menu-end bg-dark" aria-labelledby="navbarDropdownMenuLink">
							<li><router-link class="dropdown-item bg-dark text-white" to='/login'>{{MENU_2}}</router-link></li>
							<li><router-link class="dropdown-item bg-dark text-white" to='/register'>{{MENU_3}}</router-link></li>
						</ul>
					</li>
				</ul>
				<ul v-else class="navbar-nav">
					<li class="nav-item">
						<router-link class="nav-link mx-2" to='/basket'>
							<svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="currentColor" class="bi bi-cart " viewBox="0 0 16 16"> <path d="M0 1.5A.5.5 0 0 1 .5 1H2a.5.5 0 0 1 .485.379L2.89 3H14.5a.5.5 0 0 1 .491.592l-1.5 8A.5.5 0 0 1 13 12H4a.5.5 0 0 1-.491-.408L2.01 3.607 1.61 2H.5a.5.5 0 0 1-.5-.5zM3.102 4l1.313 7h8.17l1.313-7H3.102zM5 12a2 2 0 1 0 0 4 2 2 0 0 0 0-4zm7 0a2 2 0 1 0 0 4 2 2 0 0 0 0-4zm-7 1a1 1 0 1 1 0 2 1 1 0 0 1 0-2zm7 0a1 1 0 1 1 0 2 1 1 0 0 1 0-2z"/></svg>
							<span class="badge bg-dark">{{totalproduct}}</span> 
						</router-link>
					</li>
					<li class="nav-item dropdown">
						<a class="nav-link dropdown-toggle" href="{$LINK_3}" id="navbarDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false">{{MENU_5}}</a>
						<ul class="dropdown-menu dropdown-menu-end bg-dark" aria-labelledby="navbarDropdownMenuLink">
							<li><router-link class="dropdown-item bg-dark text-white" to='/logout'>{{MENU_4}}</router-link></li>
							<li><router-link class="dropdown-item bg-dark text-white" to='/myorders'>{{MENU_7}}</router-link></li>
						</ul>
					</li>
				</ul>
			</div>
		</div>
	</nav>
</div>
</template>

<script>

export default {
	data() {
		return {
			products: [],
			basket: [],
			MENU_1: 'Home',
			MENU_2: 'Log In',
			MENU_3: 'Register',
			MENU_4: 'Log Out',
			MENU_5: 'Welcome',
			MENU_6: 'Cart',
			MENU_7: 'My Orders',
			user: {
				id: '', 
				name: '', 
				email: '', 
				session_id: ''
			},
		}
	},
	mounted() {
		this.getProducts();
		this.getBasket();
	},
    methods: {
		getBasket(){
			this.basket = this.$store.getters['basket/getProducts'];
		},
		async getProducts() {
			if(await this.$store.dispatch ('products/getProductsFromDB')) {
				this.products = await this.$store.getters['products/getProducts']
			}
			let ids = []
			for(var i = 0; i < this.basket.length; i++) {
				ids.push(this.basket[i].id)
			}
			this.products = this.products.filter(p => ids.includes(p.id))
		},
		getQuantity(id){
			let result = this.basket.find(p => p.id == id)
			return result.quantity
		},
		async addProductBasket (productId){
			await this.$store.dispatch('basket/increment', productId)
			this.basket.items= await this.$store.getters['basket/getProducts']
			console.log(this.basket.items)
		}
	},
    computed: {
		userLoggedIn: function() {
			let user = this.$store.getters['user/getUser']
			for (var i in user)
			return true
			return false
		},
		totalproduct(){
			let total = 0;
			for(let i = 0; i < this.basket.length; i++) {
				total += this.getQuantity(this.basket[i].id)
			}
			return total
		}   
    },
}

</script>  
