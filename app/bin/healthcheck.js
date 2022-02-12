const http = require("http");
const options = {
  host: process.env.HOST || "0.0.0.0",
  port: process.env.PORT || 3000,
  timeout: process.env.HEALTHCHECK_TIMEOUT || 10_000,
};

const request = http.request(options, res => {
  console.log(`Response status code: ${res.statusCode}`)
  if (res.statusCode === 200) {
    process.exit(0);
  } else {
    process.exit(1);
  }
});

request.on("error", err => {
  console.error(`Error: ${("message" in err) ? err.message : "{no err.message}"}`);
  process.exit(1);
});
