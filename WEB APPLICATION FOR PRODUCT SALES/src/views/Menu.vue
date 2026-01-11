<template>
<div>
	<Header />
	<div id="demo" class="carousel slide" data-bs-ride="carousel">
		<div class="carousel-indicators">
			<button type="button" data-bs-target="#demo" data-bs-slide-to="0" class="active" aria-current="true"></button>
			<button type="button" data-bs-target="#demo" data-bs-slide-to="1" class=""></button>
			<button type="button" data-bs-target="#demo" data-bs-slide-to="2" class=""></button>
		</div>
		<div class="carousel-inner">
			<div class="carousel-item active">
				<img src="@/assets/images/Imagem1.jpg" alt="" class="d-block" style="width:100%">
			</div>
			<div class="carousel-item">
				<img src="@/assets/images/Imagem2.jpg" alt="" class="d-block" style="width:100%">
			</div>
			<div class="carousel-item">
				<img src="@/assets/images/Imagem3.jpg" alt="" class="d-block" style="width:100%">
			</div>
		</div>
		<button class="carousel-control-prev" type="button" data-bs-target="#demo" data-bs-slide="prev">
			<span class="carousel-control-prev-icon"></span>
		</button>
		<button class="carousel-control-next" type="button" data-bs-target="#demo" data-bs-slide="next">
			<span class="carousel-control-next-icon"></span>
		</button>
	</div>
	<div class="container">
		<div class="row row-cols-1 row-cols-3 g-4 mt-5">
			<div class="col" v-for="product in products" :key="product.id">
				<div class="card mb-3">
					<img v-bind:src="product.image" class="card-img-top">
					<div class="card-body">
						<h5 class="card-title">{{product.name}}</h5>
						<p class="card-text">{{product.description}}
							<span class="badge bg-secondary">{{product.price}}€</span>
						</p>
						<button type="submit" class="btn btn-danger" @click="addProductBasket(product.id)">Add to cart</button>
					</div>
				</div>
			</div>
		</div>
	</div>
	<Footer />
</div>
</template>

<script>
import Footer from '@/components/Footer.vue'
import Header from '@/components/Header.vue'


export default {
    showModal: true,
    components: {
		Footer,
        Header
	},
	data() {
		return {
		products: [],
		categorie: [],
		basket: [],
		}
	},
	mounted() {
		this.getProducts();
	},
	methods: {
		getBasket(){
			this.basket = this.$store.getters['basket/getProducts'];
		},
		async getProducts() {
			if(await this.$store.dispatch ('products/getProductsFromDB')) {
				this.products = await this.$store.getters['products/getProducts']
			}
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

<style scoped>
/* Slideshow container */
.carousel-inner{
		max-height: 700px;
		position: relative;
		margin: auto;
		}
		.footer{
            position: fixed;
            left: 0;
            right: 0;
            bottom: 0;
            width: 100%;
        }

        .author{
            float: right;
        }

		label.pad{
			padding: 1rem 1.25rem;
		}
</style>