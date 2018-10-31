* **List** is a data structure that provides CRUD operations and preserves order.
* **Tuple** is a list of comparable data with natural order. It is not aware of columns nor any other table data.
* **Table** is a list of tuples with basic sql operations. It provides access to TableHeader.
* **TableHeader** is metadata (administrative data) about a particular table. It stores information about name of 
columns, their aliases, and so on. 
