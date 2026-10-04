# Copilot instructions

## General 

## Code writing
- Use snakeCase instead of snake_case for variable now
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