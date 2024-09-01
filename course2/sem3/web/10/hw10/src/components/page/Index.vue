<template>
    <Posts :posts="viewPosts" :users="users" :comments="comments"/>
</template>

<script>
import Posts from "@/components/page/Posts.vue";

export default {
    name: "Index",
    components: {Posts},
    props: ["posts", "users", "comments"],
    computed: {
        viewPosts: function () {
            return Object.values(this.posts).sort((a, b) => b.id - a.id);
        }
    }
}
</script>

<style scoped>

</style>
