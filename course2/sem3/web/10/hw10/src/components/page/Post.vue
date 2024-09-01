<template>
    <article>
        <div class="title">
            <a href="#" @click.prevent="changePost">{{ post.title }}</a>
        </div>
        <div class="information">By {{ getUserLogin }} </div>
        <div class="body">{{ post.text }}</div>
        <div class="footer">
            <div class="right">Comments: {{ commentsCount }}</div>
        </div>
        <WriteComment v-if="userId" :postId="post.id"/>
        <Comment class="comment" v-for="comment in viewComments"
                 :comment="comment" :users="users" :needComments="needComments"
                 :key="comment.id"/>
    </article>
</template>

<script>

import Comment from "@/components/page/Comment.vue";
import WriteComment from "@/components/page/WriteComment.vue";

export default {
    name: "Post",
    components: {WriteComment, Comment},
    props: ["post", "users", "comments", "userId", "needComments"],
    computed: {
        getUserLogin: function () {
            return this.users[this.post.userId].login;
        },
        viewComments: function () {
            return Object.values(this.comments).filter(c => c.postId === this.post.id).sort((a, b) => (b.id - a.id));
        },
        commentsCount: function () {
            return Object.values(this.comments).filter(c => c.postId === this.post.id).length;
        }
    },
    methods: {
        changePost: function () {
            this.$root.$emit("onPost", this.post.id);
        }
    }
}
</script>

<style scoped>
    .comment {
        margin-bottom: 0;
    }
</style>