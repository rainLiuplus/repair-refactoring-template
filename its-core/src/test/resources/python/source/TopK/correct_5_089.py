def top_k(lst, k):
    # Fill in your code here
    a = lst
    sort = []
    while a:
        largest = a[0]
        for item in a:
            if item>largest:
                largest = item
        a.remove(largest)
        sort.append(largest)
    return(sort)[:k]
