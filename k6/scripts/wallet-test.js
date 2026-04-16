import http from 'k6/http';
import { check } from 'k6';

const wallets = JSON.parse(open('../data/wallets.json'));
const TARGET_URL = __ENV.TARGET_URL || 'http://app:8080';
console.log(`Loaded ${wallets.length} wallets`);


export const options = {
    stages: [
        { duration: '30s', target: 100 },
        { duration: '1m', target: 500 },
        { duration: '30s', target: 0 },
    ],
};

export default function () {
    const wallet = wallets[Math.floor(Math.random() * wallets.length)];
    const operation = Math.random() > 0.5 ? 'DEPOSIT' : 'WITHDRAW';

    const payload = JSON.stringify({
        walletId: wallet.id,
        operationType: operation,
        amount: 10.00,
    });

    const res = http.post(`${TARGET_URL}/api/v1/wallet`, payload, {
        headers: { 'Content-Type': 'application/json' },
    });

    check(res, {
        'status is 200': (r) => r.status === 200,
    });

}