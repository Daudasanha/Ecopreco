// Aguarda o carregamento completo do DOM antes de executar o script
document.addEventListener("DOMContentLoaded", () => {
    // Seletores dos elementos da página
    const addProductBtn = document.getElementById("add-product-btn");
    const productFormContainer = document.getElementById("product-form-container");
    const productForm = document.getElementById("product-form");
    const cancelProductBtn = document.getElementById("cancel-product-btn");
    const productTableBody = document.querySelector("#product-table tbody");

    // Armazena o ID do produto que está sendo editado
    let editingProductId = null;

    // === Carregar Produtos do Servidor ===
    async function loadProducts() {
        try {
            // Faz requisição GET para obter todos os produtos
            const data = await apiRequest("/products/public/all?size=1000", "GET");
            renderProductsTable(data.content); // Renderiza na tabela
        } catch (error) {
            console.error("Erro ao carregar produtos:", error.message);
            showMessage("message", "Erro ao carregar produtos.", "error");
        }
    }

    // === Exibir Produtos na Tabela ===
    function renderProductsTable(products) {
        productTableBody.innerHTML = "";

        // Caso não haja produtos, exibe mensagem
        if (!products || products.length === 0) {
            productTableBody.innerHTML = "<tr><td colspan='6'>Nenhum produto encontrado.</td></tr>";
            return;
        }

        // Cria uma linha para cada produto
        products.forEach(product => {
            const row = productTableBody.insertRow();
            row.innerHTML = `
                <td>${product.id}</td>
                <td>${product.name}</td>
                <td>R$ ${product.currentPrice.toFixed(2)}</td>
                <td>${product.brand}</td>
                <td>${product.category}</td>
                <td>
                    <button class="btn-edit" onclick="editProduct(${product.id})">Editar</button>
                    <button class="btn-delete" onclick="deleteProduct(${product.id})">Excluir</button>
                </td>
            `;
        });
    }

    // === Botão "Adicionar Produto" ===
    addProductBtn.addEventListener("click", () => {
        productForm.reset(); // Limpa o formulário
        editingProductId = null; // Garante que não está em modo de edição
        productFormContainer.style.display = "block"; // Mostra o formulário
        document.getElementById("product-form-title").textContent = "Adicionar Novo Produto";
    });

    // === Botão "Cancelar" ===
    cancelProductBtn.addEventListener("click", () => {
        productForm.reset(); // Limpa o formulário
        productFormContainer.style.display = "none"; // Esconde o formulário
    });

    // === Envio do Formulário de Produto (Salvar ou Atualizar) ===
    productForm.addEventListener("submit", async (event) => {
        event.preventDefault(); // Evita o comportamento padrão

        // Captura os dados do formulário
        const productData = {
            name: document.getElementById("product-name").value.trim(),
            description: document.getElementById("product-description").value.trim(),
            currentPrice: parseFloat(document.getElementById("product-price").value),
            brand: document.getElementById("product-brand").value.trim(),
            category: document.getElementById("product-category").value.trim(),
            ecoLabel: document.getElementById("product-ecolabel").value.trim(),
            productUrl: document.getElementById("product-url").value.trim(),
            imageUrl: document.getElementById("product-image-url").value.trim(),
            store: document.getElementById("product-store").value.trim(),
            sustainabilityInfo: document.getElementById("product-sustainability-info").value.trim()
        };

        // Validação básica
        if (!productData.name || !productData.currentPrice) {
            showMessage("message", "Nome e preço são obrigatórios.", "error");
            return;
        }

        try {
            if (editingProductId) {
                // Se estiver editando, envia PUT
                await apiRequest(`/products/${editingProductId}`, "PUT", productData, true);
                showMessage("message", "Produto atualizado com sucesso!", "success");
            } else {
                // Se for novo produto, envia POST
                await apiRequest("/products", "POST", productData, true);
                showMessage("message", "Produto adicionado com sucesso!", "success");
            }

            productFormContainer.style.display = "none"; // Esconde o formulário
            loadProducts(); // Atualiza a tabela

        } catch (error) {
            console.error("Erro ao salvar produto:", error.message);
            showMessage("message", error.message, "error");
        }
    });

    // === Função para Editar Produto ===
    window.editProduct = async (id) => {
        try {
            console.log("Tentando carregar produto com ID:", id);

            const product = await apiRequest(`/products/${id}`, "GET"); // Busca dados do produto

            console.log("Resposta da API:", product);

            // Verifica se produto retornado é válido
            if (!product || !product.id) {
                throw new Error("Produto não encontrado ou resposta inválida.");
            }

            // Preenche os campos do formulário com os dados do produto
            document.getElementById("product-id").value = product.id;
            document.getElementById("product-name").value = product.name || "";
            document.getElementById("product-description").value = product.description || "";
            document.getElementById("product-price").value = product.currentPrice || "";
            document.getElementById("product-brand").value = product.brand || "";
            document.getElementById("product-category").value = product.category || "";
            document.getElementById("product-ecolabel").value = product.ecoLabel || "";
            document.getElementById("product-url").value = product.productUrl || "";
            document.getElementById("product-image-url").value = product.imageUrl || "";
            document.getElementById("product-store").value = product.store || "";
            document.getElementById("product-sustainability-info").value = product.sustainabilityInfo || "";

            editingProductId = product.id; // Define que está em modo de edição
            productFormContainer.style.display = "block"; // Exibe o formulário

        } catch (error) {
            console.error("Erro ao carregar produto para edição:", error.message);
            showMessage("message", error.message, "error");
        }
    };

    // === Função para Excluir Produto ===
    window.deleteProduct = async (id) => {
        if (confirm("Tem certeza que deseja excluir este produto?")) {
            try {
                await apiRequest(`/products/${id}`, "DELETE", null, true); // Envia requisição DELETE
                showMessage("message", "Produto excluído com sucesso!", "success");
                loadProducts(); // Atualiza a tabela
            } catch (error) {
                console.error("Erro ao excluir produto:", error.message);
                showMessage("message", error.message, "error");
            }
        }
    };

    // === Verifica se o Usuário Está Logado e Tem Permissão ===
    if (isLoggedIn()) {
        const userRoles = JSON.parse(localStorage.getItem("userRoles"));
        if (userRoles && userRoles.includes("ROLE_ADMIN")) {
            loadProducts(); // Somente ADMIN pode carregar os produtos
        } else {
            showMessage("message", "Acesso negado. Apenas administradores podem acessar esta página.", "error");
            setTimeout(() => window.location.href = "index.html", 3000); // Redireciona
        }
    } else {
        showMessage("message", "Você precisa estar logado para acessar esta página.", "error");
        setTimeout(() => window.location.href = "login.html", 3000); // Redireciona
    }
});

// === Verifica se o Token de Login Existe no LocalStorage ===
function isLoggedIn() {
    return !!localStorage.getItem("jwtToken");
}

// === Exibe Mensagens de Erro ou Sucesso ===
function showMessage(elementId, message, type) {
    const element = document.getElementById(elementId);
    if (!element) return;

    element.textContent = message;
    element.className = `message ${type}`;
    element.style.display = "block";

    // Esconde a mensagem após 5 segundos
    setTimeout(() => {
        element.style.display = "none";
    }, 5000);
}
