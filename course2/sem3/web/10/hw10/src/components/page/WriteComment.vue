<template>
    <div class="form">
        <div class="header">Write Comment</div>
        <div class="body">
            <form @submit.prevent="onWriteComment">
                <div class="field">
                    <div class="name">
                        <label for="text">Text:</label>
                    </div>
                    <div class="value">
                        <textarea id="text" name="text" v-model="text"></textarea>
                    </div>
                </div>
                <div class="error">{{ error }}</div>
                <div class="button-field">
                    <input type="submit" value="Write">
                </div>
            </form>
        </div>
    </div>
</template>

<script>
export default {
    name: "WriteComment",
    props: ["postId"],
    data: function () {
        return {
            text: "",
            error: ""
        }
    },
    methods: {
        onWriteComment: function () {
            this.error = "";
            this.$root.$emit("onWriteComment", this.text, this.postId);
            this.text = "";
        }
    },
    beforeMount() {
        this.$root.$on("onWriteCommentValidationError", error => this.error = error);
        this.$root.$on("onPost", () => { this.text = "" });
    }
}
</script>

<style scoped>
    .body {
        padding-left: 0;
        border-left: 0;
    }
</style>