# CompaJ REPL

REPL can evaluate scripts in console from string or file.

# Run

## Local

To run REPL locally in your console just run jar file:

```bash
java -jar repl.jar
```

## Docker
Image on Dockerhub: `kpekepsalt/compaj-repl:latest`

To run REPL in Docker container use `Dockerfile` in this repository or pull image from docker hub:

### x64

```bash
docker run -dit kpekepsalt/compaj-repl:latest --platform=linux/amd64
```

### arm64 (Apple Silicon)

```bash
docker run -dit kpekepsalt/compaj-repl:latest --platform=linux/arm64
```

Container built with minimal JRE 11 to reduce size and exported PATH to repl:
```bash
compaj -s "println 5"
```

### Supported ARCH

- linux/amd64
- linux/arm64

## Arguments

- -f fileUrl - evaluate script from given file
- -s script - evaluate script from given string

Example:

```bash
java -jar repl.jar -f "path/to/file"
java -jar repl.jar -s "println 5 * 2"
```

# Build Docker image
Multiplatform image can be built with existing Dockerfile using buildx:
```bash
docker buildx build --platform linux/amd64,linux/arm64 -t <username>/<imagename>:<tag> .
```