<template>
<div>
	<section class="bg-image">
        <div class="mask d-flex align-items-center h-100 gradient-custom-3">
            <div class="container h-100">
				<div class="row d-flex justify-content-center align-items-center h-100">
					<div class="col-12 col-md-9 col-lg-7 col-xl-6">
						<div class="card mt-5" style="border-radius: 20px;">
							<div class="card-body p-5">
								<h1 class="text-center">{{message}}</h1>
							</div>
						</div>
					</div>
				</div>
			</div>
        </div>
    </section>
</div>
</template>

<script>
export default {
    data() {
		return {
            message: '',
		}
	},   
    mounted() {

	},
	setMessage() {
		if (this.$route.params.id == 4) {
			this.message = "Welcome! You can now login"
		}
		if (this.$route.params.id == 5) {
			this.message = "Welcome back " + this.$store.getters['user/getUser'].name + "!"
		}
		if (this.$route.params.id == 6) {
			this.message = "Bye, come back soon!"
		}
		var timeleft = 2;
		var downloadTimer = setInterval(() => {
			if(timeleft <= 0){
				clearInterval(downloadTimer)
				this.$router.push('/menu')
			}
			timeleft -= 1
		}, 1000)
		},
}
</script>

<style scoped>
.bg-image {
		background-image: src = "@/assets/images/Imagem4.png";
		background-size: cover;
		height: 100vh;
	}
	.logout {
	margin: 0 auto;
	max-width: 800px;
	}
	.success-message {
	color: #32a95d;
	}
	.error-message {
	color: #d33c40;
	}
</style>



