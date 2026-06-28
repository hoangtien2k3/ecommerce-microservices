"use client";

import { Search } from "lucide-react";
import { Input } from "../Input";

interface SearchBarProps {
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
  className?: string;
}

export function SearchBar({ value, onChange, placeholder, className }: SearchBarProps) {
  return (
    <Input
      placeholder={placeholder ?? "Search..."}
      value={value}
      onChange={(e) => onChange(e.target.value)}
      leftIcon={<Search className="h-4 w-4" />}
      className={className}
    />
  );
}
