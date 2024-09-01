<template>
    <div class="middle">
        <Sidebar :posts="viewPosts"/>
        <main>
            <Index v-if="page === 'Index'" :posts="posts" :users="users" :comments="comments"/>
            <Enter v-if="page === 'Enter'"/>
            <Register v-if="page === 'Register'"/>
            <Users v-if="page === 'Users'" :users="users"/>
            <WritePost v-if="page === 'WritePost'"/>
            <EditPost v-if="page === 'EditPost'"/>
            <Post v-if="page === 'Post'"
                  :post="post" :users="users"
                  :comments="comments" :userId="userId" :need-comments="true"/>
        </main>
    </div>
</template>

<script>
import Sidebar from "./sidebar/Sidebar";
import Index from "./page/Index";
import Enter from "./page/Enter";
import WritePost from "./page/WritePost";
import EditPost from "./page/EditPost";
import Register from "@/components/page/Register.vue";
import Users from "@/components/page/Users.vue";
import Post from "@/components/page/Post.vue";

export default {
    name: "Middle",
    data: function () {
        return {
            page: "Index",
            post: null
        }
    },
    components: {
        Post,
        Users,
        Register,
        WritePost,
        Enter,
        Index,
        Sidebar,
        EditPost
    },
    props: ["posts", "users", "comments", "userId"],
    computed: {
        viewPosts: function () {
            return Object.values(this.posts).sort((a, b) => b.id - a.id).slice(0, 2);
        }
    }, beforeCreate() {
        this.$root.$on("onChangePage", (page) => this.page = page);
        this.$root.$on("onPost", (postId) => {
            this.page = 'Post';
            this.post = this.posts[postId];
        });
    }
}
</script>

<style scoped>

</style>
