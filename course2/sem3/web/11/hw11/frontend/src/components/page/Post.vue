<template>
    <article>
        <div class="title">
            <a href="#" @click.prevent="changePost">{{ post.title }}</a>
        </div>
        <div class="information">By {{ getUserLogin }} </div>
        <div class="body">
            <p v-for="(t, ind) in post.text.split('\n')" :key="ind">{{ t }}</p>
        </div>
        <div class="footer">
            <div class="right">Comments: {{ getPostComments.length }}</div>
        </div>
        <Comment class="comment" v-for="comment in getPostComments"
                 :comment="comment" :needComments="needComments"
                 :key="comment.id"/>
    </article>
</template>

<script>
import Comment from "@/components/page/Comment.vue";

export default {
    name: "Post",
    components: {Comment},
    props: ["post", "comments", "needComments"],
    computed: {
        getUserLogin: function () {
            return this.post.user.login;
        },
        getPostComments: function () {
            return this.comments.filter(c => c.post.id === this.post.id);
        }
    },
    methods: {
        changePost: function () {
            this.$root.$emit("onPost", this.post);
        }
    }
}
</script>

<style scoped>
.comment {
    margin-bottom: 0;
}
</style>