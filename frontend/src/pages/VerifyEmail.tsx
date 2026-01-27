import React, { useEffect, useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import { useSearchParams, Link } from 'react-router-dom';

export const VerifyEmail: React.FC = () => {
    const { verifyEmail } = useAuth();
    const [searchParams] = useSearchParams();
    const token = searchParams.get('token');
    const [status, setStatus] = useState<'verifying' | 'success' | 'error'>('verifying');

    useEffect(() => {
        if (!token) {
            setStatus('error');
            return;
        }

        verifyEmail(token)
            .then(() => setStatus('success'))
            .catch(() => setStatus('error'));
    }, [token, verifyEmail]);

    return (
        <div className="flex flex-col items-center justify-center min-h-[60vh]">
            <div className="bg-white p-8 rounded-xl shadow-sm border border-gray-100 text-center max-w-md w-full">
                {status === 'verifying' && (
                    <>
                        <h2 className="text-xl font-bold mb-4 text-gray-800">Verifying Email...</h2>
                        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600 mx-auto"></div>
                    </>
                )}

                {status === 'success' && (
                    <>
                        <h2 className="text-xl font-bold mb-4 text-green-600">Email Verified! ✅</h2>
                        <p className="text-gray-600 mb-6">Your account is active. You can now login.</p>
                        <Link to="/login" className="bg-indigo-600 text-white px-6 py-2 rounded hover:bg-indigo-700">Login</Link>
                    </>
                )}

                {status === 'error' && (
                    <>
                        <h2 className="text-xl font-bold mb-4 text-red-600">Verification Failed ❌</h2>
                        <p className="text-gray-600 mb-6">The link may be expired or invalid.</p>
                        <Link to="/register" className="text-indigo-600 hover:underline">Back to Registration</Link>
                    </>
                )}
            </div>
        </div>
    );
};
