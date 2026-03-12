package model.parser.dataClasses

fun <ProductType> Node.flattenThisAndDescendantsTo(
    depth: Int = 0,
    parentProduct: ProductType? = null,
    generateProduct: (currentNode: Node, depth: Int, parentProduct: ProductType?) -> ProductType,
): List<ProductType> {
    val result = mutableListOf<ProductType>()

    val currentProduct = generateProduct(this, depth, parentProduct)
    result.add(currentProduct)

    children.forEach {
        val childResult =
            it.flattenThisAndDescendantsTo(
                depth = depth + 1,
                parentProduct = currentProduct,
                generateProduct,
            )
        result += childResult
    }

    return result
}
