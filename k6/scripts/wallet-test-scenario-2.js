import http from 'k6/http';
import { check } from 'k6';

const SINGLE_WALLET = '550e8400-e29b-41d4-a716-446655440000';
const TARGET_URL = __ENV.TARGET_URL || 'http://app:8080';

export const options = {
    scenarios: {
        single_wallet_test: {
            executor: 'constant-arrival-rate',
            rate: 2000,
            timeUnit: '1s',
            duration: '2m',
            preAllocatedVUs: 200,
            maxVUs: 500,
        },
    },
};

export default function () {
    const operation = Math.random() > 0.5 ? 'DEPOSIT' : 'WITHDRAW';

    const payload = JSON.stringify({
        walletId: SINGLE_WALLET,
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