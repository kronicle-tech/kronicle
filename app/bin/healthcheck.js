const http = require("http");
const options = {
  host: "localhost",
  port: 3000,
  path: "/health",
  timeout: 10_000,
};

console.log(`Options: ${JSON.stringify(options)}`)

const req = http.request(options, res => {
  console.log(`Response status code: ${res.statusCode}`)
  if (res.statusCode === 200) {
    process.exit(0);
  } else {
    process.exit(1);
  }
});

req.on("error", err => {
  console.error(`Error: ${("message" in err) ? err.message : "{no err.message}"}`);
  process.exit(1);
});

req.end();
