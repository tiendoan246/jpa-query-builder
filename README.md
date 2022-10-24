# Query structure

we support expression below:

# String:

eq = exactly equal

contains = like a in bab

in = in a list “abc” in [“abc”, “cde“]

# Boolean

eq = exactly equal

# Date (yyyy-MM-dd) date only not include time

eq = equals

gt = great than

gte = great than equal

lt = less than

lte = less than equal

between = between date (“2022-10-10“ <= value <= “2022-10-15“)

# Number (Integer and Float)

eq = equals

gt = great than

gte = great than equal

lt = less than

lte = less than equal

in = in

```shell
request: {
    filter: {
        firstName: { in : ["ilfnwhbzbjdfeek", "jauhcsecvjqwmbm"] },
        phoneRegistered: {eq: "+84527158115"},
        or: {
          lastName: {eq: "uttwsvcqjvurmnf"},
          resourceId: {eq: "8d5c8a0c-cdd1-4048-90e5-e6a31376d212"}
        }
    },
    page: {
        limit: 10,
        offset: 0
    },
    sort: {
        sortBy: [
            "firstName",
            "lastName"
        ],
        order: "asc"
    }
}
```


this will translate to:

```shell
firstName IN ("ilfnwhbzbjdfeek", "jauhcsecvjqwmbm") AND phoneRegistered = "+84527158115"
AND (lastName = "uttwsvcqjvurmnf" OR resourceId = "8d5c8a0c-cdd1-4048-90e5-e6a31376d212")

ORDER BY firstName then by lastName ASC

OFFSET 0 LIMIT 10
```

# Query contains three parts:

# filter: <optional>

filter “firstName" map with column name in database for example database column “first_name“ now in query “firstName“.

firstName, phoneRegistered combine to “and” logical operator

```shell
or: {
  lastName: {eq: "uttwsvcqjvurmnf"},
  resourceId: {eq: "8d5c8a0c-cdd1-4048-90e5-e6a31376d212"}
}
``` 

this will equal with and (firstname = “uttwsvcqjvurmnf“ or resourceId = “8d5c8a0c-cdd1-4048-90e5-e6a31376d212“)

```shell
and: {
  lastName: {eq: "uttwsvcqjvurmnf"},
  resourceId: {eq: "8d5c8a0c-cdd1-4048-90e5-e6a31376d212"}
}
``` 

this will equal with and (firstname = “uttwsvcqjvurmnf“ and resourceId = “8d5c8a0c-cdd1-4048-90e5-e6a31376d212“)

# page: <optional> with default paging 20 items if not present.

# sort: <optional>


Example for user listing API

```shell
{
    searchUsers(request: {
        filter: {
            firstName: { in : ["ilfnwhbzbjdfeek", "jauhcsecvjqwmbm"] },
            phoneRegistered: {eq: "+84527158115"},
            or: {
              lastName: {eq: "uttwsvcqjvurmnf"},
              resourceId: {eq: "8d5c8a0c-cdd1-4048-90e5-e6a31376d212"}
            }
        },
        page: {
            limit: 10,
            offset: 0
        },
        sort: {
            sortBy: [
                "firstName",
                "lastName"
            ],
            order: "asc"
        }
    }) {
    data {              // fields you want to return from response
            resourceId                  
            firstName
            lastName
            phoneRegistered
            partner {
                resourceId
                name
            }
        }
        totalElements        // Total elements in database
        totalPages           // Total pages
    }
}
```