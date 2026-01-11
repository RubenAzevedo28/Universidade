<template>
<div>
	<Header />
	<section class="vh-100 background">
		<div class="mask d-flex align-items-center h-100">
			<div class="container h-100">
				<div class="row d-flex justify-content-center align-items-center h-100">
					<ol class="list-group list-group-numbered mt-5">
						<li class="list-group-item d-flex justify-content-between align-items-start" v-for="product in products" :key="product.id">
							<div class="ms-2 me-auto">
								<div class="fw-bold">
									{{product.name}}
								</div>
								<div>
									Total de items:
									<button class = "btn btn-light btn-sm" @click="removeProductBasket(product.id)"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-dash-circle" viewBox="0 0 16 16"> <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"/><path d="M4 8a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7A.5.5 0 0 1 4 8z"/></svg></button>
									<button class = "btn btn-light btn-sm" @click="addProductBasket(product.id)"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-plus-circle" viewBox="0 0 16 16"><path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"/><path d="M8 4a.5.5 0 0 1 .5.5v3h3a.5.5 0 0 1 0 1h-3v3a.5.5 0 0 1-1 0v-3h-3a.5.5 0 0 1 0-1h3v-3A.5.5 0 0 1 8 4z"/></svg></button>
									{{getQuantity(product.id)}}
								</div>
								<span class="badge bg-secondary rounded-pill">{{product.price}}€</span>
							</div>
						</li>
					</ol>
					<div class="card bg-dark p-4">
						<h4 class="text-center text-white">Total a pagar: <span class="badge bg-secondary">{{totalvalue}}€</span></h4>
					</div>
					<div v-if="!userLoggedIn" class="card-body">
						<router-link to="/login" class="text-body">
							<div class="d-flex justify-content-center btn-m">
								<button type="submit" class="btn btn-outline-light btn-m">Login to Checkout</button>
							</div>
						</router-link>
					</div>
					<div v-else class="card-body">
						<router-link to="/myorders" class="text-body">
							<div class="d-flex justify-content-center">
								<button type="submit" class="btn btn-outline-light btn-m">Checkout</button>
							</div>
						</router-link>
					</div>
				</div>
			</div>
		</div>
	</section>
	<Footer />
</div>
</template>

<script>
import Footer from '@/components/Footer.vue'
import Header from '@/components/Header.vue'

export default {
	name: 'products',
    components: {
		Footer,
        Header
	},
	data() {
		return {
			products: [],
			basket: [],
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
			if (await this.$store.dispatch('products/getProductsFromDB')) {
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
		getTotalPrice(id){
			let quantity = this.getQuantity(id)
			let result = this.products.find(p => p.id == id)
			return quantity * result.price
		},
		async addProductBasket(productID){
			await this.$store.commit('basket/incrementProduct', productID)
			this.basket.items = await this.$store.getters['basket/getProducts']
			this.getProducts
		},
		async removeProductBasket(productID){
			await this.$store.commit('basket/decrementProduct', productID)
			this.basket.items = await this.$store.getters['basket/getProducts']
			this.getProducts
		},
	},
    computed: {
		userLoggedIn: function () {
			let user = this.$store.getters['user/getUser']
			for (var i in user)
			return true
			return false
		},
		totalvalue(){
			let total = 0;
			for(let i = 0; i < this.products.length; i++) {
				total += this.getTotalPrice(this.products[i].id)
			}
			return total
		}
	},
}
</script>

<style scoped>
.background {
Background-image: url("../assets/images/Imagem4.jpg")
}
</style>