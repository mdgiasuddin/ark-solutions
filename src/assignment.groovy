import java.text.DecimalFormat

// Necessary classes for storing the information
class Product {
    private String name
    private String group
    private double cost
    private double price

    Product(String name, String group, double cost, double price) {
        this.name = name
        this.group = group
        this.cost = cost
        this.price = price
    }

    void setPrice(double price) {
        this.price = price
    }

    String getGroup() {
        return group
    }

    double getCost() {
        return cost
    }

    double getPrice() {
        return price
    }
}

class Category {
    private double minPrice
    private double maxPrice
    private double margin

    Category(double minPrice, double maxPrice, double margin) {
        this.minPrice = minPrice
        this.maxPrice = maxPrice
        this.margin = margin
    }

    void setMargin(double margin) {
        this.margin = margin
    }

    double getMinPrice() {
        return minPrice
    }

    double getMaxPrice() {
        return maxPrice
    }

    double getMargin() {
        return margin
    }

}

class Group {
    private double totalPrice
    private int numberOfProducts

    Group(double totalPrice, int numberOfProducts) {
        this.totalPrice = totalPrice
        this.numberOfProducts = numberOfProducts
    }

    void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice
    }

    void setNumberOfProducts(int numberOfProducts) {
        this.numberOfProducts = numberOfProducts
    }

    double getTotalPrice() {
        return totalPrice
    }

    int getNumberOfProducts() {
        return numberOfProducts
    }
}


// Method for calculating average price for each group
static List<List> calculatePrice(List<Product> productList, List<Category> categoryList) {
    for (product in productList) {
        double margin = getMarginOfCategory(categoryList, product.getCost())

        product.setPrice(product.getCost() * (1 + margin))
    }

    Map<String, Group> groupMap = new HashMap<>()
    for (product in productList) {
        if (groupMap.containsKey(product.getGroup())) {
            Group group = groupMap.get(product.getGroup())
            group.setTotalPrice(group.getTotalPrice() + product.getPrice())
            group.setNumberOfProducts(group.getNumberOfProducts() + 1)
        } else {
            groupMap.put(product.getGroup(), new Group(product.getPrice(), 1))
        }
    }

    DecimalFormat f = new DecimalFormat("##.0");

    List<List> result = new ArrayList<>()
    for (entry in groupMap) {
        result.add(Arrays.asList(entry.getKey(), Double.parseDouble(f.format(entry.getValue().totalPrice / entry.getValue().numberOfProducts))))
    }

    return result
}


// Method for finding margin for category using binary search
static double getMarginOfCategory(List<Category> categoryList, double cost) {
    int left = 0, right = categoryList.size() - 1;

    while (left <= right) {
        int mid = (int) (left + (right - left) / 2)

        Category category = categoryList.get(mid);
        if (cost >= category.getMinPrice() && cost <= category.getMaxPrice()) {
            return category.getMargin()
        }
        if (cost < category.getMinPrice()) {
            right = mid - 1
        } else {
            left = mid + 1
        }
    }

    return 0
}


// Sample data for testing
def products = [
        ["A", "G1", 20.1],
        ["B", "G2", 98.4],
        ["C", "G1", 49.7],
        ["D", "G3", 35.8],
        ["E", "G3", 105.5],
        ["F", "G1", 55.2],
        ["G", "G1", 12.7],
        ["H", "G3", 88.6],
        ["I", "G1", 5.2],
        ["J", "G2", 72.4]]

def category = [
        ["C3", 50, 75],
        ["C4", 75, 100],
        ["C2", 25, 50],
        ["C5", 100, null],
        ["C1", 0, 25]]

def margins = [
        "C1": "20%",
        "C2": "30%",
        "C3": "0.4",
        "C4": "50%",
        "C5": "0.6"]

// Build up an list from the product data
List<Product> productList = new ArrayList<>()
for (product in products) {
    productList.add(new Product(product[0] as String, product[1] as String, product[2] as double, 0));
}

// Build up category list from category data
// Used hashmap because we have to set margin from another array of category data
Map<String, Category> categoryMap = new HashMap<>();
for (cg in category) {
    Category ctg = new Category(cg[1] as double, cg[2] == null ? Double.MAX_VALUE : cg[2] as double, 0)

    categoryMap.put(cg[0] as String, ctg)
}

// Set margin for each category
for (entry in margins) {
    if (categoryMap.containsKey(entry.getKey())) {
        Category ctg = categoryMap.get(entry.getKey())
        String margin = entry.getValue();

        double marginValue
        if (margin.endsWith("%")) {
            marginValue = Double.parseDouble(margin.substring(0, margin.length() - 1)) / 100
        } else {
            marginValue = Double.parseDouble(margin)
        }

        ctg.setMargin(marginValue)
    }
}

// Build up a category list from hashmap
List<Category> categoryList = new ArrayList<>()
for (entry in categoryMap) {
    categoryList.add(entry.getValue())
}

// Sort the category list according to minimum price range for the purpose of binary search
categoryList.sort((c1, c2) -> c1.getMinPrice() - c2.getMinPrice())


// Finally call the method for calculating price of each group
List<List> result = calculatePrice(productList, categoryList)

println(result)


