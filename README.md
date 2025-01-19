# EcoSim - Ecosystem Simulation

EcoSim is a virtual ecosystem simulation built in Java that models the dynamics of a natural environment, where entities interact, evolve, and adapt to seasonal changes and resource limitations. This project demonstrates the application of complex system modeling, agent-based simulation, and adaptive behavior, all structured with clean code principles and scalable design patterns.

## Project Highlights

* Complex Ecosystem Simulation: Entities compete for resources, evolve through genetic mutations, and face natural challenges such as predation and seasonal changes.
* Real-time Monitoring: Graphical representations of population trends, interactions, and evolutionary progress, providing valuable insights into ecosystem dynamics.
* Genetic Evolution: Entities evolve over generations with mutations that affect their characteristics, including speed, energy, and vision range.
* Interactive Environment: Users can take control of an entity and influence the outcome of the ecosystem by directly interacting with it.
* Robust Architecture: Designed with modularity and scalability in mind, featuring a well-structured codebase that separates concerns efficiently.

## Core Architecture

The project is divided into two main packages that organize the system logically:

### Core

* GameManager: Coordinates the ecosystem simulation, manages the game state, and handles resource and population initialization.
* Game: Controls the temporal evolution of the system, driving entity behaviors and interactions over time.
* InteractionManager: Manages all entity interactions, including predation, reproduction, and environmental responses.
* GraphManager: Visualizes statistical data, such as population changes and evolutionary trends, allowing real-time analysis of the ecosystem.

### Model

* World: Defines the structure of the ecosystem, organizing locations and resource availability through the WorldMap class.
* Factory: Uses the Factory design pattern to create entities like blobs, trees, and fruits, promoting clean code practices and easy system expansion.
* Entity: Houses all entities within the ecosystem, including:
    * Animals: Blobs (both passive and predator types), each with distinct survival behaviors.
    * Plants: Trees and other fixed environmental elements that affect the ecosystem.
    * Resources: Fruits and other consumables that contribute to entity survival.

## Gameplay Overview

The simulation follows the life cycle of entities within the ecosystem, including:

* Movement and Behavior: Entities exhibit behaviors like seeking food, running from predators, and wandering.
* Genetic Evolution: Entities evolve over generations through mutations, affecting their survival and adaptation.
* Seasonal Changes: Graphs demonstrate the influence of seasons on entity survival and resource availability.
* Interactivity: Users can control a specific entity, guiding it through the challenges posed by the ecosystem.

## Key Features

* Adaptive Evolution: Entities evolve based on genetic mutations that affect traits like speed, energy, and vision.
* Real-Time Graphs: View population trends, seasonal changes, and genetic data via interactive graphs.
* Ecosystem Balance: Adjust initial fruit numbers and mutation rates to stabilize the ecosystem and prevent overpopulation or extinction.

## Technical Skills Demonstrated

### Data Structures

* HashMap: Efficiently manages the locations of entities within the ecosystem.
* ArrayList: Used to track entities, resources, and interactions dynamically.

### Design Patterns

* Factory: Simplifies the creation of entities (e.g., blobs, trees, fruits) to maintain scalability and flexibility.
* Singleton: Ensures single instances of game management and ecosystem control components.

### OOP Principles

* Encapsulation, Inheritance, and Polymorphism: Applied throughout the codebase to manage entity behaviors and interactions.
* Modularity: Ensures a clean, maintainable structure with clear separation of responsibilities.

## Visuals

### Class Dependency Overview

Visualize the relationships between major classes in the project to understand the system architecture.

### Comprehensive Class Diagram

An in-depth class diagram depicting the structure and hierarchy of the project's classes.

### Sequence Diagram - Game Interaction

Illustrates the interactions between core classes during gameplay, including the evolution and interaction of entities.

## Future Enhancements

* Expanded Biomes: Introduce diverse ecosystems with varying resources and challenges.
* Advanced Genetic Algorithms: Implement more complex genetic mutation models for more realistic evolution.
* Improved Interaction Models: Introduce additional behaviors and interactions for entities, including complex predation and symbiosis.

## How to Run

1. Clone the repository.
2. Compile and execute using `java -jar ecosim-game.jar`.
