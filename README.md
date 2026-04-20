# Static Hash File Application

This project is a simple Java application implementing a **block-oriented static hash file** with overflow handling.

The application stores records representing municipalities (city name, population, altitude) using a **hashing mechanism** and **fixed-size blocks** stored in a binary file.

---

## Features

- Generate at least **10,000 random records**
- Insert a new record
- Find a record by key (municipality name)
- Delete a record
- Display all records
- Show file structure information
- Handle collisions using **overflow blocks**

---

## Data Structure

Each record contains:

- `String name` (key)
- `int population`
- `int altitude`

Records are stored in:

- Fixed-size blocks
- Each block contains multiple records
- Blocks may reference an **overflow block** if full

---

## How It Works

1. The **hash function** maps a key (name) to a block index.
2. The application reads the corresponding block from file.
3. If the record is not found:
    - It follows the overflow chain.
4. If inserting:
    - If block is full → create/use overflow block.
5. All operations work with **blocks**, not individual records.

---
## How to Run

Compile the project:
   javac *.java


Run:
   java Main


The program will:

- Generate 10,000 random records
- Store them in a hash file
- Open a GUI window

---

## GUI

The application provides a simple interface to:

- Search municipality
- Insert new record
- Delete record
- Refresh and display all data

---




