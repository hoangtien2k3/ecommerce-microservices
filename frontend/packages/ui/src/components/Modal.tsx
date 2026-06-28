"use client";

import { X } from "lucide-react";
import { cn } from "@ecommerce/lib/utils";
import { useEffect, useCallback } from "react";

interface ModalProps {
  open: boolean;
  onClose: () => void;
  title?: string;
  children: React.ReactNode;
  className?: string;
}

export function Modal({ open, onClose, title, children, className }: ModalProps) {
  const handleKeyDown = useCallback(
    (e: KeyboardEvent) => {
      if (e.key === "Escape") onClose();
    },
    [onClose]
  );

  useEffect(() => {
    if (open) document.addEventListener("keydown", handleKeyDown);
    return () => document.removeEventListener("keydown", handleKeyDown);
  }, [open, handleKeyDown]);

  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div
        className={cn(
          "bg-white rounded-2xl w-full max-w-lg shadow-xl animate-in fade-in zoom-in-95",
          className
        )}
      >
        {title && (
          <div className="flex items-center justify-between p-6 border-b">
            <h3 className="text-lg font-bold">{title}</h3>
            <button onClick={onClose} className="p-1.5 rounded-lg hover:bg-gray-100">
              <X className="h-5 w-5" />
            </button>
          </div>
        )}
        {children}
      </div>
    </div>
  );
}

export function ModalBody({ children }: { children: React.ReactNode }) {
  return <div className="p-6 space-y-4">{children}</div>;
}

export function ModalFooter({ children }: { children: React.ReactNode }) {
  return <div className="flex gap-3 p-6 border-t">{children}</div>;
}
