<template>
<div>
    <section class="vh-100 background">
        <div class="mask d-flex align-items-center h-100 gradient-custom-3">
            <div class="container h-100">
                <div class="row d-flex justify-content-center align-items-center h-100">
                    <div class="col-12 col-md-9 col-lg-7 col-xl-6">
                        <div class="card bg-dark mt-5" style="border-radius: 20px;">
                            <div class="card-body p-5">
                                <h3 class="text-center text-white mb-5">Do you want to Log Out?</h3>
                                <div class="d-flex justify-content-center">
                                    <button @click="logout()" class="btn btn-outline-light mx-2 ">Logout</button>
                                    <button @click="cancel()" class="btn btn-outline-light mx-2 ">Cancel</button>
                                </div>
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
//import Footer from '@/components/Footer.vue'

export default {
    /*components: {
		Footer,
	},*/
    data() {
        return {
            success: false,
            user: {},
        }
    },
    methods: {
        logout: function() {
            this.success = true
            if (this.userLoggedIn) {
                // destroy session
                this.logoutUser(this.user.session_id)
            }
            else {
                // No user is signed in.
                this.$router.push('/')
            }
        },
        async logoutUser(session_id) {
            if ( await this.$store.dispatch('user/logoutUser', session_id) ) {
                this.$router.push('/message/6')
            }
        },
        cancel: function(){
            this.$router.push('/')
        },
        getUser() {
            this.user = this.$store.getters['user/getUser']
        },
    },
    
    created: function () {
        if (this.userLoggedIn) {
            // User is signed in
            this.success = false
        }
        else {
            // No user is signed in.
            this.success = true
        }
    },
    computed: {
        userLoggedIn: function () {
            this.getUser()
            for (var i in this.user)
            return true
            return false
        }
    }
}
</script>

<style scoped>
.background {
Background-image: url("../assets/images/Imagem4.jpg")
} 
</style>
