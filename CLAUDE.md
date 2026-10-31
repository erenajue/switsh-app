# Copilot instructions

## General 
All UI experience is described on application prototype directory "prototype"

## Code writing

### Split into separate files:
- Any large class, or one with real logic (a ViewModel, repository, service, or Activity/Fragment). Each gets its own file named after it.
- Unrelated classes that just happen to be used in the same screen or feature.
- Anything that grows past a few hundred lines, where finding things starts to get tedious.
- Public types that other modules or features depend on, so they are easy to find by name.

### Naming:

- A file with a single class is named after that class (UserRepository.kt).
- A file with several declarations gets a descriptive UpperCamelCase name for its contents, such as UserModels.kt or NetworkErrors.kt.
- Use snakeCase instead of snake_case for variable now

### Technical stack
- Use SQL lite as internal database
- Use Ktor as REST client library
  - Consider adding 10 seconds timeouts
  - all REST calls must require authentication token
  - authentication tokens are meant not to last more than 7mins
  - include in each persistence call (POST, PATCH, PUT, DELETE) a correlationId in request header

## Post task updates
- After a task from file task.md is complete:
  - Mark the task as completed in file task.md
  - Update the README.md file if necessary