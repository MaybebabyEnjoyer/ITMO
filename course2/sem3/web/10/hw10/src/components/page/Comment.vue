<template>
    <article v-if="needComments">
        <div class="information">Comment by {{ getUserLogin }}</div>
        <div class="body">
            <div v-for="(t, ind) in comment.text.split('\n')" :key="ind">{{ t }}</div>
        </div>
    </article>
</template>

<script>
export default {
    name: "Comment",
    props: ["comment", "users", "needComments"],
    computed: {
        getUserLogin: function () {
            return this.users[this.comment.userId].login;
        }
    }
}
</script>

<style scoped>

</style>