
[//]: # (TODO Add a contribuntion rules file)

# About

This lstream library provides both

- an API for sequential only stream
- an implementation based on iterators

# Motivation

## Problem of Stream API

Stream API is a very powerful, functional way of managing collections of objects. Its parallelisation usage offers a
great way to iterate with high performance.  
However, parallelisation is used in very few cases an everyday project, but the parallel API still exists : it is
inconvenient to be forced to write a merge function when reducing a sequential stream.

## Problem of Iterator

Iterator is a very understandable way of iterating over a collection.  
However, it lacks some functional API to get more modern.

## Conclusion

This library defines the same API as the Stream one but without the parallelisation.  
It also offers an implementation with iterators.

# Usage

## Maven dependency

[//]: # (TODO Explain how to add the maven dependency)

## Code examples

### Filter even numbers and sort

Let `numbers` be a list of integers. We want to keep only even ones, sort, and create a new immutable list.

```java
LStream.from(numbers).filter(n -> n%2 == 0).sorted().toList();
```
