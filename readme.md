Collections [![Build Status](https://travis-ci.org/briangordon/collections.svg?branch=master)](https://travis-ci.org/briangordon/collections)
===========

This is a collection of collections data structures written in Java 7. These are accompanied by extensive tests which prove the correctness of the implementations.

Implemented
-----------

Name                   | Description
---------------------- | -------------
LinkedQueue            | A simple doubly-linked queue for single-threaded applications.
SynchronizedArrayQueue | A thread-safe array-backed queue which uses coarse-grained locking.

Pending
-------

Name                    | Description
----------------------- | -------------
LockFreeLinkedQueue     | A lock-free doubly-linked queue for highly concurrent applications.
LockFreeLinkedStack     | A lock-free doubly-linked stack for highly concurrent applications.
SynchronizedLinkedQueue | A thread-safe doubly-linked queue which uses fine-grained locking to allow simultaneous enqueues and dequeues.

License
-------

All code in this repository is licensed under the MIT/Expat license, which can be found in the `LICENSE` file.