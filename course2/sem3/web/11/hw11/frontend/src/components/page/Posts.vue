<template>
    <div>
        <Post v-for="post in posts"
              :post="post" :comments="comments" :needComments="false" :key="post.id"/>
    </div>
</template>

<script>
import Post from "@/components/page/Post.vue";

export default {
    name: "Posts",
    components: {Post},
    props: ["posts", "comments"]
}
</script>

<style scoped>

</style>