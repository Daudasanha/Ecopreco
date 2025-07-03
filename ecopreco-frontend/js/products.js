document.addEventListener("DOMContentLoaded", () => {
    // Obtém referências aos elementos da interface para manipulação
    const productList = document.getElementById("product-list"); // Container onde os produtos serão exibidos
    const searchInput = document.getElementById("search-input"); // Campo de busca por palavra-chave
    const categoryFilter = document.getElementById("category-filter"); // Select para filtro por categoria
    const brandFilter = document.getElementById("brand-filter"); // Select para filtro por marca
    const storeFilter = document.getElementById("store-filter"); // Select para filtro por loja
    const ecoLabelFilter = document.getElementById("eco-label-filter"); // Select para filtro por selo ecológico
    const minPriceFilter = document.getElementById("min-price-filter"); // Campo para preço mínimo
    const maxPriceFilter = document.getElementById("max-price-filter"); // Campo para preço máximo
    const minRatingFilter = document.getElementById("min-rating-filter"); // Campo para avaliação mínima
    const applyFiltersBtn = document.getElementById("apply-filters-btn"); // Botão para aplicar filtros

    // Variáveis para paginação
    let currentPage = 0; // Página atual (começa da 0)
    const pageSize = 12; // Quantidade de produtos exibidos por página

    // Função assíncrona que carrega produtos com base nos filtros aplicados
    async function loadProducts() {
        try {
            // URL padrão para listar todos produtos paginados
            let url = `/products/public/all?page=${currentPage}&size=${pageSize}`;
            
            // Obtém os valores dos filtros, removendo espaços extras
            const keyword = searchInput.value.trim();
            const category = categoryFilter.value.trim();
            const brand = brandFilter.value.trim();
            const store = storeFilter.value.trim();
            const ecoLabel = ecoLabelFilter.value.trim();
            const minPrice = minPriceFilter.value.trim();
            const maxPrice = maxPriceFilter.value.trim();
            const minRating = minRatingFilter.value.trim();

            // Cria objeto URLSearchParams para montar query string com filtros
            const params = new URLSearchParams();
            if (keyword) params.append("keyword", keyword);
            if (category) params.append("category", category);
            if (brand) params.append("brand", brand);
            if (store) params.append("store", store);
            if (ecoLabel) params.append("ecoLabel", ecoLabel);
            if (minPrice) params.append("minPrice", minPrice);
            if (maxPrice) params.append("maxPrice", maxPrice);
            if (minRating) params.append("minRating", minRating);

            // Caso haja filtros, altera a URL para endpoint de filtro ou busca
            if (params.toString()) {
                // Endpoint padrão para filtro com paginação
                url = `/products/public/filter?${params.toString()}&page=${currentPage}&size=${pageSize}`;
                // Caso haja palavra-chave, usa o endpoint de busca
                if (keyword) {
                    url = `/products/public/search?keyword=${encodeURIComponent(keyword)}&${params.toString()}&page=${currentPage}&size=${pageSize}`;
                }
            }

            // Faz requisição à API (função apiRequest deve estar definida em outro lugar)
            const data = await apiRequest(url);

            // Renderiza os produtos recebidos na página
            renderProducts(data.content);

            // Atualiza as opções dos filtros dinamicamente
            loadFilterOptions(); // Pode ser melhor chamar isso separadamente para evitar múltiplas chamadas desnecessárias

        } catch (error) {
            // Caso ocorra erro, exibe no console e mostra mensagem para o usuário
            console.error("Erro ao carregar produtos:", error);
            showMessage("message", "Erro ao carregar produtos.", "error");
        }
    }

    // Função para exibir os produtos no HTML
    function renderProducts(products) {
        productList.innerHTML = ""; // Limpa a lista de produtos atual
        if (!products || products.length === 0) {
            // Caso não haja produtos, exibe mensagem
            productList.innerHTML = "<p>Nenhum produto encontrado.</p>";
            return;
        }

        // Para cada produto, cria um card com suas informações
        products.forEach(product => {
            const productCard = document.createElement("div");
            productCard.classList.add("product-card");

            // Monta o conteúdo HTML do card com dados do produto
            productCard.innerHTML = `
                <img src="${product.imageUrl || 'https://via.placeholder.com/300x200?text=Sem+Imagem'}" 
                     alt="${product.name}" 
                     onerror="this.src='https://via.placeholder.com/300x200?text=Imagem+Não+Encontrada'; this.onerror=null;">
                
                <div class="product-card-content">
                    <h4>${product.name}</h4>
                    <p><strong>Marca:</strong> ${product.brand || 'N/A'}</p>
                    <p><strong>Categoria:</strong> ${product.category || 'N/A'}</p>
                    <p><strong>Loja:</strong> ${product.store || 'N/A'}</p>
                    <p><strong>Selo Eco:</strong> ${product.ecoLabel || 'Nenhum'}</p>
                    <p><strong>Avaliação:</strong> ${product.averageRating ? product.averageRating.toFixed(1) : 'N/A'}</p>
                    <div class="price">R$ ${product.currentPrice.toFixed(2)}</div>
                    
                    <div class="actions">
                        <button onclick="window.open('${product.productUrl}', '_blank')">Ver Produto</button>
                        <button class="btn-secondary" onclick="viewProductDetails(${product.id})">Detalhes</button>
                    </div>
                </div>
            `;

            // Adiciona o card na lista de produtos na página
            productList.appendChild(productCard);
        });
    }

    // Função para carregar as opções dos filtros (categoria, marca, loja, selo ecológico)
    async function loadFilterOptions() {
        try {
            // Busca categorias na API e preenche o select
            const categories = await apiRequest("/products/public/categories");
            populateSelect(categoryFilter, categories);

            // Busca marcas e preenche
            const brands = await apiRequest("/products/public/brands");
            populateSelect(brandFilter, brands);

            // Busca lojas e preenche
            const stores = await apiRequest("/products/public/stores");
            populateSelect(storeFilter, stores);

            // Busca selos ecológicos e preenche
            const ecoLabels = await apiRequest("/products/public/ecolabels");
            populateSelect(ecoLabelFilter, ecoLabels);

        } catch (error) {
            console.error("Erro ao carregar opções de filtro:", error);
        }
    }

    // Função para preencher um select com opções dinâmicas
    function populateSelect(selectElement, options) {
        selectElement.innerHTML = '<option value="">Todas</option>'; // Opção padrão "Todas"
        options.forEach(option => {
            if (option) {
                const opt = document.createElement("option");
                opt.value = option; // valor da opção
                opt.textContent = option; // texto exibido
                selectElement.appendChild(opt);
            }
        });
    }

    // Adiciona event listener no botão para aplicar os filtros
    applyFiltersBtn.addEventListener("click", () => {
        currentPage = 0; // Reseta para primeira página quando filtros são aplicados
        loadProducts(); // Recarrega produtos com os filtros atualizados
    });

    // Carrega os produtos quando a página é carregada
    loadProducts();

    // Função global para mostrar detalhes de um produto (ainda simples, só exibe alert)
    window.viewProductDetails = (productId) => {
        alert(`Ver detalhes do produto ID: ${productId}`);
        // Futuramente, pode redirecionar para página de detalhes do produto
        // ex: window.location.href = `produto.html?id=${productId}`;
    };
});

// Função global para exibir mensagens temporárias ao usuário
function showMessage(elementId, message, type) {
    const element = document.getElementById(elementId);
    if (!element) return; // Se o elemento não existir, não faz nada
    element.textContent = message; // Atualiza texto da mensagem
    element.className = `message ${type}`; // Aplica classe para estilo (ex: error, success)
    element.style.display = "block"; // Mostra a mensagem
    setTimeout(() => {
        element.style.display = "none"; // Esconde após 5 segundos
    }, 5000);
}
