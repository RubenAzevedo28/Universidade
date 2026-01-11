<template>
<div>
	<Header />
	<section class="vh-100 background">
		<div class="mask d-flex align-items-center h-100 gradient-custom-3">
			<div class="container h-100">
				<div class="row d-flex justify-content-center align-items-center h-100">
					<div class="col-12 col-md-9 col-lg-7 col-xl-6">
						<div class="card bg-dark mt-5" style="border-radius: 20px;">
							<div v-if="!userLoggedIn" class="card-body p-5">
								<h2 class="text-uppercase text-center text-white mb-5">Create an account</h2>
								<div v-if="error" class="text-bg-danger text-center mb-1">{{warning}}</div>
								<form @submit.prevent="handleSubmit">
									<div class="form-floating mb-3">
										<input type="text" class="form-control" id="name" placeholder="Name" v-model="user.name" v-autofocus @focus="clearStatus" @keypress="clearStatus" name="name" required>
										<label for="name">Name</label>
									</div>
									<div class="form-floating mb-3">
										<input type="email" class="form-control" id="email" placeholder="name@example.com" v-model="user.email" @focus="clearStatus" name="email" required>
										<label for="email">Email Address</label>
									</div>
									<div class="form-floating mb-3">
										<input type="password" class="form-control" id="pwd" placeholder="Password" v-model="user.password" @focus="clearStatus" name="pwd" required>
										<label for="pwd">Password</label>
									</div>
									<div class="form-floating mb-3">
										<input type="password" class="form-control" id="c_pwd" placeholder="Password" v-model="passwordConfirmation" @focus="clearStatus" name="c_pwd" required>
										<label for="c_pwd">Confirm Password</label>
									</div>
									<div class="d-flex justify-content-center mb-3">
										<button type="submit" class="btn btn-outline-light btn-block btn-m">Sign Up</button>
									</div>
									<p class="text-center text-muted mt-5 mb-0">Already have an account? <router-link class="fw-bold text-white" to='/login'><u>Login here</u></router-link></p>
								</form>
							</div>
						</div>
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
	components: {
		Footer,
        Header
	},
	data() {
      return {	
        user: {
			name: '',
			email: '',
			password: '',
        },
		passwordConfirmation: '',
		submitting: false,
		error: false,
		warning:'',
      }
    },
	
	methods: {
		handleSubmit: function () {
			this.submitting = true	
			this.clearStatus()
			if (this.invalidName) {
				this.warning = "Your username can't include digits"
				this.error = true
				return
			}
			else if(this.invalidEmail){
				this.warning = "Please enter a valid email"
				this.error = true
				return
			}
			else if(this.invalidPassword){
				this.warning = "Your password must have at least 6 characters"
				this.error = true
				return
			}
			else if(this.invalidRepeatPassword){
				this.warning = "Passwords don't match"
				this.error = true
				return
			}
			// check if user exists
			this.userExists(this.user)
		},
		clearStatus: function () {
			this.error = false
		},
		async userExists(user) {
			if ( await this.$store.dispatch('user/userExists', user) )	{
				this.error = true
				this.submitting = false
				return
			}
			else {
				// add user
				this.addUser()
			}
		},
		async addUser() {
			if ( await this.$store.dispatch('user/addUser') ) {
				//success: new user registered
				this.$router.push('/message/4')
			}
			else {
				this.error = true
				this.submitting = false							
				return
			}
		},
		cancel() {
			this.$router.push('/')
		}
	},

	computed: {
		invalidName: function() {
			if (this.user.name.match(/[0-9]/g)!=null)
			return true
			else
			return false
		},
		invalidPassword: function() {
			if (this.user.password.length < 6)
			return true
			else
			return false
		},
		invalidRepeatPassword: function() {
			if (this.submitting && this.user.password !== this.passwordConfirmation)
			return true
			else
			return false
		},
		invalidEmail: function() {
			const regExpr = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/
			if (!this.user.email.match(regExpr))
			return true
			else
			return false
		},
		userLoggedIn: function() {
			let user = this.$store.getters['user/getUser']
			for (var i in user)
			return true
			return false
		},
	},
	directives: {
		autofocus: {
			inserted(el) {
				el.focus()
			}
		}
	},
	created: function () {
		this.submitting = false
		this.error = false
	}
}
</script>

<style scoped>
.background {
Background-image: url("../assets/images/Imagem4.jpg")
} 
</style>
