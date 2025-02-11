@import com.sts.demo.enums.Role
@import gg.jte.Content
@import java.util.List
@param List<Role> roles

<!DOCTYPE html>
<html>
<head>
    <title>Create User</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="p-6">
<h2 class="text-2xl font-bold">Create User</h2>

<form method="post" action="/admin/users/create" class="mt-4">
    <label class="block">Username</label>
    <input type="text" name="username" class="border p-2 w-full">

    <label class="block mt-2">Password</label>
    <input type="password" name="password" class="border p-2 w-full">

    <label class="block mt-2">Role</label>
    <select name="role" class="border p-2 w-full">
        @for (Role role : roles))
            <option value="@role">@role</option>
        @endfor
    </select>

    <button type="submit" class="mt-4 bg-blue-500 text-white py-2 px-4">Create User</button>
</form>
</body>
</html>
