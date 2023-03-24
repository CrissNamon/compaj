# CompaJ REPL

REPL can evaluate scripts in console from string or file.

# Run

## Local
To run REPL locally in your console just run jar file:

```bash
java -jar repl.jar
```

## Docker
To run REPL in Docker container use `Dockerfile` in this repository or pull image from docker hub: 
```bash
docker pull kpekepsalt/compaj-repl
```

## Arguments

- -f <fileUrl> - evaluate script from given file
- -s <script> - evaluate script from given string

Example:
```bash
java -jar repl.jar -f "path/to/file"
java -jar repl.jar -s "println 5 * 2"
```