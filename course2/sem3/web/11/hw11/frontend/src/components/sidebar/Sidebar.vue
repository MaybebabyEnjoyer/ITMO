<template>
    <aside>
        <SidebarPost v-for="post in posts" :post="post" :key="post.id"/>
    </aside>
</template>

<script>
import SidebarPost from "./SidebarPost";

export default {
    name: "Sidebar",
    components: {
        SidebarPost
    },
    props: ["posts"]
}
</script>

<style scoped>

</style>
