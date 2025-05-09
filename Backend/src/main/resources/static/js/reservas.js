document.addEventListener('DOMContentLoaded', function () {
    const preco = parseFloat(document.getElementById('preco').value.replace(',', '.'));
    const quantidadeInput = document.getElementById('quantidade');
    const valorTotal = document.getElementById('valor-total');

    // Atualiza o valor total ao alterar a quantidade
    quantidadeInput.addEventListener('input', function () {
        const quantidade = parseInt(quantidadeInput.value) || 0;
        const total = (quantidade * preco).toFixed(2).replace('.', ',');
        valorTotal.textContent = total;
    });

    // Inicializa com o valor correto ao carregar a página
    const quantidade = parseInt(quantidadeInput.value) || 0;
    const total = (quantidade * preco).toFixed(2).replace('.', ',');
    valorTotal.textContent = total;
});
