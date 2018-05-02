# LiveAdapter
LiveAdapter is a library which helps you to keep your `RecylcerView` and the items within it up to date. Simply add and remove items to the list and the UI is updated automagicly includeing animations. You never have to call a single `notify...Changed()` method again.

The library respects the Android [lifecycle](https://developer.android.com/topic/libraries/architecture/lifecycle) and was created due to [this question](https://stackoverflow.com/questions/48336448/observe-add-and-remove-events-from-arraylist) on Stack Overflow. It's very easy to use and has some nice additinal features like filtering, a better `RecylcerView` and some material design resources. On the other hand you implement your Adapter like you always would do so you are free to implement anything you want.

## How to include into your project
Add it in your root build.gradle at the end of repositories:

```
allprojects {
    repositories {
        // ...
        maven { url 'https://jitpack.io' }
    }
}
```
Add this to your project dependencies:
```
implementation 'com.github.Cilenco:LiveAdapter:1.0.0'
```
## How to use this library

### 1. Implement your item
Implement your item as simple POJO class which implement `IViewItem`. For a default implementation you can also extend the `ViewItem` class. To use a simplified adapter class make sure to implement the `Compareable<T>` interface.

```java
public class Book extends ViewItem<Book> {
    public String title;
    public String author;

    public int compareTo(Book other) {
        return title.compareTo(other.title);
    }
}
```

### 2. Implement your ViewModel
The `ViewModel` class is part of the Architecture Components library and is responsible for preparing our data for the UI. The [ViewModel](https://developer.android.com/reference/android/arch/lifecycle/ViewModel.html) objects are automatically retained during configuration changes so that data they hold is immediately available to the next activity or fragment instance. You can read all about it [here](https://developer.android.com/topic/libraries/architecture/viewmodel.html).

```java
public class BookViewHolder extends ViewModel {
    private ObservableArrayList<Book> books;

    public ApplicationViewHolder() {
        applications = new ObservableArrayList<>();
    }

    public ObservableArrayList<Application> getBooks() {
        return applications;
    }
}
```
  
### 3. Implement your adapter
Just create a class which extends `SimpleLifecycleAdapter` and implement it as you would normally do. To use this class your Model must implement the `Compareable<T>` interface or extend the `ViewModel` class which gives you . If you don't want to do either one of them, you can also extend `BaseLifecycleAdapter` where you have to implement a three abstract methods which will act as `Comperator`.

```java
public class BookAdapter extends SimpleBaseAdapter<Book, BookAdapter.ItemHolder> {

    public ApplicationAdapter(LifecycleOwner owner, ObservableList<Book> items) {
        super(owner, items, Book.class);
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View contactView = inflater.inflate(R.layout.item_one_line, parent, false);
        
        return new ItemHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        Book book = getItem(position);

        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText(app.getAuthor());
    }

    protected class ItemHolder extends SimpleViewHolder {
        TextView tvTitle;
        TextView tvAuthor;

        ItemHolder(View itemView) {
            super(itemView);
            
            tvTitle = itemView.findViewById(R.id.title);
            tvAuthor = itemView.findViewById(R.id.author);
        }
    }
}
```

### 4. Set the Adapter to the RecyclerView
```java
bookViewModel = ViewModelProviders.of(this).get(BookViewHolder.class);
BookAdapter adapter = new BookAdapter(this, appViewModel.getBooks());

adapter.setOnItemClickListener(this);
recyclerView.setAdapter(adapter);
```

### 5. Filter
```java
adapter.setFilterPredicate((item, query) -> item.getTitle().contains(query));

public boolean onQueryTextChange(String search) {
    adapter.filter(search);
    return true;
 }
```

### 6. Drag & Drop / Swipe to dismiss
```java
SimpleDragCallback dragCallback = new SimpleDragCallback(this);
ItemTouchHelper touchHelper = new ItemTouchHelper(dragCallback);
touchHelper.attachToRecyclerView(recyclerView);
```

## Additional Extras

### SimpleRecyclerView
The library provides a custom `RecyclerView` which is already setup with a `VERTICAL` LayoutManager and the default `ItemDecoration`. This `RecyclerView` can automatically show a placeholder view when the adapter does not contain any items. Simply call the new method `setPlaceholder(...)` on it.

### Material design layouts
Both, the SingleLineItem and the TwoLineItem layouts are included in the library. To provide a simple `ViewHolder` setup there are also the corresponding classes `SingleLineViewHolder` and `TwoLineViewHolder`. Use them as shown below.

```java
protected class ItemHolder extends SingleLineViewHolder {
    protected View getActionView() {
        checkBox = new CheckBox(itemView.getContext());
    }
}
```

## Contribute
Whenever you find bugs or have any optimizations fell free to send me a pull request.

## License
