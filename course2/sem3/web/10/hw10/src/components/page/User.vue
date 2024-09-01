<template>
    <tr>
        <td class="id">{{ user.id }}</td>
        <td class="login">{{ user.login }}</td>
        <td class="username">{{ user.name }}</td>
    </tr>
</template>

<script>
export default {
    name: "User",
    props: ["user"]
}
</script>

<style scoped>

</style>