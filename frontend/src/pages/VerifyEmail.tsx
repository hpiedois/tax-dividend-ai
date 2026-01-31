import React from 'react';
import { Link } from 'react-router-dom';

export const VerifyEmail: React.FC = () => {
    return (
        <div className="flex flex-col items-center justify-center min-h-[60vh]">
            <div className="bg-white p-8 rounded-xl shadow-sm border border-gray-100 text-center max-w-md w-full">
                <h2 className="text-xl font-bold mb-4 text-gray-800">Email Verification</h2>
                <p className="text-gray-600 mb-6">Verification is handled by our Identity Provider. Please check your email.</p>
                <Link to="/login" className="bg-indigo-600 text-white px-6 py-2 rounded hover:bg-indigo-700">Login</Link>
            </div>
        </div>
    );
};
