<template>
<div>
	<Header />
	<section class="vh-100 background">
        <div class="mask d-flex align-items-center h-100">
            <div class="container h-100">
                <div class="row d-flex justify-content-center align-items-center h-100">
                    <div class="col-12 col-md-9 col-lg-7 col-xl-6">
                        <div class="card bg-dark mt-5" style="border-radius: 20px;">
                            <div v-if="!userLoggedIn" class="card-body p-5">
                                <h2 class="text-uppercase text-center text-white mb-5">Log In</h2>
								<div v-if="error" class="text-bg-danger text-center mb-1">{{warning}}</div>
                                <form @submit.prevent="handleSubmit">
                                    <div class="form-floating mb-3">
                                        <input type="email" class="form-control" id="floatingInput" placeholder="name@example.com" v-model="user.email" name="email" @focus="clearStatus" required>
                                        <label for="floatingInput">Email Address</label>
                                    </div>
                                    <div class="form-floating mb-3">
                                        <input type="password" class="form-control" id="floatingPassword" placeholder="Password" v-model="user.password" name="pwd" @focus="clearStatus" required>
                                        <label for="floatingPassword">Password</label>
                                    </div>
                                    <div class="d-flex justify-content-center mb-3">
                                        <button type="submit" class="btn btn-outline-light btn-m">Sign In</button>
                                    </div>
                                    <div class="d-flex justify-content-center mb-3">
                                        <input type="checkbox" class="btn btn-check-success mx-1" name="remember"> 
                                        <label class="text-white">Remember me</label>
                                    </div>
                                    <p class="text-center text-muted mt-5 mb-0">Don't have an account? <router-link class="fw-bold text-white" to='/register'><u>Register here</u></router-link></p>
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
			email: '',
			password: '',
        },
		submitting: false,
		error: false,
		warning: '',
      }
    },
	
	methods: {
		handleSubmit: function() {
			this.submitting = true
			this.clearStatus()
			if(this.invalidEmail || this.invalidPassword) {
				this.error = true
				return
			}
			this.loginUser (this.user)
		},
		clearStatus: function() {
			this.error = false
		},
		async loginUser(user) {
			if(await this.$store.dispatch('user/loginUser', user)) {
				//login valid
				this.$router.push('/message/5')
			}
			else {
				//login failed
				this.error = true
				this.submitting = false
				this.warning = "Wrong Email or Password"
			}
		},
		cancel(){
			this.$router.push('/')
		},
	},
	
	computed: {
		invalidPassword: function(){
			if(this.user.password === '')
			return true
			else 
			return false
		},
		invalidEmail: function(){
			if(this.user.email === '' || this.user.email.search['@'] === -1)
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
			inserted(el){
				el.focus()
			}
		}
	},
	created: function () {
		this.submitting = false
		this.error = false
	}
}
</script>\	

<style scoped>
.background {
Background-image: url("../assets/images/Imagem4.jpg")
}
</style>
