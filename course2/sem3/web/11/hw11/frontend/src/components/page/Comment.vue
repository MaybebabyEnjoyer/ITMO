<template>
    <article v-if="needComments">
        <div class="information">Comment by {{ getUserLogin }}</div>
        <div class="body">
            <p v-for="(t, ind) in comment.text.split('\n')" :key="ind">{{ t }}</p>
        </div>
    </article>
</template>

<script>
export default {
    name: "Comment",
    props: ["comment", "needComments"],
    computed: {
        getUserLogin: function () {
            return this.comment.user.login;
        }
    }
}
</script>

<style scoped>

</style>