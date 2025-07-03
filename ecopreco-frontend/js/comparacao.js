// === comparacao.js ===
// Este script permite ao usuário comparar dois produtos a partir de uma lista carregada da API.

document.addEventListener("DOMContentLoaded", async () => {
    // Obtém referências aos elementos da página
    const product1Select = document.getElementById("product1-select");
    const product2Select = document.getElementById("product2-select");
    const compareBtn = document.getElementById("compare-btn");
    const comparisonResultsDiv = document.getElementById("comparison-results");
    const product1DetailsDiv = document.getElementById("product1-details");
    const product2DetailsDiv = document.getElementById("product2-details");

    // Lista que armazenará todos os produtos carregados da API
    let allProducts = [];

    // Carrega todos os produtos e preenche os campos de seleção (selects)
    async function loadAllProductsForSelects() {
        try {
            // Requisição para buscar todos os produtos disponíveis (limitado a 1000 para garantir a cobertura)
            const data = await apiRequest("/products/public/all?size=1000");
            allProducts = data.content;

            // Preenche os selects com os produtos retornados
            populateProductSelect(product1Select, allProducts);
            populateProductSelect(product2Select, allProducts);
        } catch (error) {
            console.error("Erro ao carregar produtos para comparação:", error);
            showMessage("message", "Erro ao carregar produtos para comparação.", "error");
        }
    }

    // Preenche um elemento <select> com a lista de produtos
    function populateProductSelect(selectElement, products) {
        selectElement.innerHTML = "<option value=\"\">Selecione um produto</option>";
        products.forEach(product => {
            const option = document.createElement("option");
            option.value = product.id;
            option.textContent = product.name;
            selectElement.appendChild(option);
        });
    }

    // Renderiza os detalhes de um produto em um container HTML
    function renderProductDetails(element, product) {
        if (!product) {
            element.innerHTML = "<p>Nenhum produto selecionado.</p>";
            return;
        }

        element.innerHTML = `
            <img src="${product.imageUrl || 'https://via.placeholder.com/200'}" alt="${product.name}">
            <div class="product-card-content">
                <h4>${product.name}</h4>
                <p><strong>Preço:</strong> R$ ${product.currentPrice.toFixed(2)}</p>
                <p><strong>Marca:</strong> ${product.brand}</p>
                <p><strong>Categoria:</strong> ${product.category}</p>
                <p><strong>Loja:</strong> ${product.store}</p>
                <p><strong>Selo Eco:</strong> ${product.ecoLabel || 'N/A'}</p>
                <p><strong>Avaliação Média:</strong> ${product.averageRating ? product.averageRating.toFixed(1) : 'N/A'}</p>
                <p><strong>Info Sustentabilidade:</strong> ${product.sustainabilityInfo || 'N/A'}</p>
                <p><a href="${product.productUrl}" target="_blank">Ver na Loja</a></p>
            </div>
        `;
    }

    // Evento de clique no botão de comparação
    compareBtn.addEventListener("click", () => {
        const product1Id = product1Select.value;
        const product2Id = product2Select.value;

        // Validação: exige seleção de dois produtos distintos
        if (!product1Id || !product2Id) {
            showMessage("message", "Por favor, selecione dois produtos para comparar.", "error");
            comparisonResultsDiv.style.display = "none";
            return;
        }

        // Busca os objetos dos produtos selecionados
        const product1 = allProducts.find(p => p.id == product1Id);
        const product2 = allProducts.find(p => p.id == product2Id);

        // Renderiza os detalhes de ambos os produtos
        renderProductDetails(product1DetailsDiv, product1);
        renderProductDetails(product2DetailsDiv, product2);

        // Exibe o container com os resultados da comparação
        comparisonResultsDiv.style.display = "block";
    });

    // Inicializa carregando todos os produtos disponíveis
    loadAllProductsForSelects();
});

