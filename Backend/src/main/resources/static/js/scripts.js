function confirmDelete() {
    return confirm('Tem certeza que deseja excluir este item?');
}

function togglePassword() {
    const passwordField = document.getElementById('password');
    const toggleBtn = document.querySelector('.toggle-password');

    if (passwordField.type === 'password') {
        passwordField.type = 'text';
        toggleBtn.textContent = 'Ocultar';
    } else {
        passwordField.type = 'password';
        toggleBtn.textContent = 'Mostrar';
    }
}


document.addEventListener('DOMContentLoaded', function() {
    console.log('Aplicação CinemaApp carregada');
});