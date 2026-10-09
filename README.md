# kubsei-editor-java

2D editor projects (GraphQL). Verifies the JWTs issued by kubsei-users; authorization is checked per resolver.
Contract: [kubsei-editor-lib-java](https://github.com/kubsei/kubsei-editor-lib-java) ships `graphql/schema.graphqls` on the classpath.

## Run

```bash
export JWT_SECRET=...            # same in users, editor and gateway
mvn spring-boot:run              # http://localhost:8082/graphql, /graphiql
```

| Variable | Default |
|---|---|
| `MONGODB_URI` | `mongodb://localhost:27017/kubsei_editor` |
| `JWT_SECRET` | required |
| `JWT_ISSUER` | `kubsei` |
| `GRAPHIQL_ENABLED` | `true` |
| `SERVER_PORT` | `8082` |
