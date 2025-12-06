# Shooter PID Tuning Guide for Smaller Motors

## Understanding Your Motor

For **smaller shooter motors** (like NEO 550, Falcon 500, or smaller TalonFX), typical characteristics:
- Lower max RPM (usually 5,000-10,000 RPM free speed)
- Less torque than larger motors
- Faster response time
- Lower power consumption

## Base Values for Smaller Shooter

### 1. Target Velocity (RPS)

**For a smaller shooter motor at ~28% power:**

| Motor Type | Typical RPM at 28% | RPS Value | Notes |
|------------|-------------------|-----------|-------|
| NEO 550 | 1,200-1,800 RPM | 20-30 RPS | Good for lightweight game pieces |
| Falcon 500 | 1,500-2,000 RPM | 25-33 RPS | Common FRC shooter motor |
| Small TalonFX | 1,000-1,500 RPM | 17-25 RPS | Depends on gearing |
| Kraken (smaller) | 1,800-2,400 RPM | 30-40 RPS | Higher speed capability |

**Recommended Starting Value:**
```java
public static final double kShooterTargetVelocityRPS = -25.0; // Start here, adjust based on testing
```

**Why negative?** Your constant shows `-0.28` (reverse), so use negative RPS to match direction.

### 2. PID Gains - Starting Points

**For smaller motors, start with these conservative values:**

```java
// Conservative starting values for smaller motors
public static final double kShooterP = 0.05;  // Lower P for smoother response
public static final double kShooterI = 0.0;   // Start with 0, add if needed
public static final double kShooterD = 0.0;   // Start with 0, add for overshoot control
```

**PID Gain Guidelines:**

| Gain | Small Motor Range | Effect | When to Increase |
|------|------------------|--------|------------------|
| **kP** | 0.03 - 0.15 | Response speed | Too slow to reach target |
| **kI** | 0.0 - 0.001 | Eliminates steady-state error | Consistent undershoot |
| **kD** | 0.0 - 0.01 | Reduces overshoot | Oscillating around target |

**Warning Signs:**
- **Too high kP**: Motor oscillates, overshoots target
- **Too low kP**: Slow to reach target, sluggish response
- **kI too high**: Motor oscillates, never settles
- **kD too high**: Motor becomes jittery, overcorrects

### 3. Feedforward Values

**Feedforward helps the motor reach target faster and maintain speed under load.**

```java
// Starting feedforward values for smaller motors
public static final double kShooterKS = 0.0;  // Static friction (usually 0 for smaller motors)
public static final double kShooterKV = 0.10; // Velocity feedforward - CRITICAL for shooters
```

**kV (Velocity Feedforward) Guidelines:**

| Motor Type | Typical kV Range | Notes |
|------------|-----------------|-------|
| NEO 550 | 0.08 - 0.12 | Lower kV, less powerful |
| Falcon 500 | 0.10 - 0.15 | Medium kV |
| Small TalonFX | 0.08 - 0.12 | Similar to NEO |
| Kraken | 0.12 - 0.18 | Higher kV, more powerful |

**How kV works:**
- kV = Volts needed per RPS
- If target is 25 RPS and kV = 0.10, feedforward provides ~2.5V
- Higher kV = motor needs more voltage to reach speed
- **kV is the most important value for consistent shooter speed!**

### 4. Complete Example Configuration

**For a typical smaller shooter (NEO 550 or Falcon 500):**

```java
public static final class ShooterConstants {
    public static final int kShooterMotorPort = 3;
    
    // Target velocity - adjust based on testing
    // Start at 25 RPS, measure actual RPM, adjust accordingly
    public static final double kShooterTargetVelocityRPS = -25.0; // Negative for reverse
    
    // PID gains - conservative starting values
    public static final double kShooterP = 0.05;  // Start low, increase if too slow
    public static final double kShooterI = 0.0;   // Add 0.0001-0.001 if steady-state error
    public static final double kShooterD = 0.0;   // Add 0.001-0.005 if overshooting
    
    // Feedforward - critical for consistent speed
    public static final double kShooterKS = 0.0;  // Usually 0 for smaller motors
    public static final double kShooterKV = 0.10; // Most important! Tune this first
}
```

## Tuning Process

### Step 1: Measure Baseline (28% Power)
1. Set motor to 28% power (your current `-0.28`)
2. Use Phoenix Tuner or SmartDashboard to measure actual RPM
3. Let it run for 5-10 seconds to stabilize
4. Record the RPM value

### Step 2: Convert to RPS
```
RPS = RPM / 60
Example: 1,500 RPM ÷ 60 = 25 RPS
```

### Step 3: Set Initial kV
Start with kV = 0.10, then adjust:
- **If motor runs too fast**: Lower kV (try 0.08)
- **If motor runs too slow**: Raise kV (try 0.12)
- **Goal**: kV should get you close to target without PID

### Step 4: Tune PID
1. **Start with kP = 0.05**
   - If too slow: increase to 0.08, then 0.10
   - If oscillating: decrease to 0.03

2. **Add kI if needed** (steady-state error)
   - Start with 0.0001
   - Increase gradually if still undershooting

3. **Add kD if overshooting**
   - Start with 0.001
   - Increase if still overshooting

### Step 5: Test Under Load
- Shoot game pieces and measure consistency
- Adjust kV if speed drops under load
- Fine-tune PID for faster response

## Quick Reference: Values by Motor Size

### Very Small (NEO 550, small gearbox)
```java
kShooterTargetVelocityRPS = -20.0;  // ~1,200 RPM
kShooterP = 0.04;
kShooterKV = 0.08;
```

### Small-Medium (Falcon 500, standard)
```java
kShooterTargetVelocityRPS = -25.0;  // ~1,500 RPM
kShooterP = 0.05;
kShooterKV = 0.10;
```

### Medium (Kraken, larger gearbox)
```java
kShooterTargetVelocityRPS = -30.0;  // ~1,800 RPM
kShooterP = 0.06;
kShooterKV = 0.12;
```

## Common Issues & Solutions

### Issue: Motor never reaches target speed
**Solution**: 
- Increase kV (try 0.12-0.15)
- Increase kP (try 0.08-0.10)
- Check if motor is mechanically limited

### Issue: Motor overshoots and oscillates
**Solution**:
- Decrease kP (try 0.03-0.04)
- Add kD (try 0.002-0.005)
- Decrease kV slightly

### Issue: Speed inconsistent when shooting
**Solution**:
- Increase kV (more feedforward)
- Add small kI (0.0001-0.0005)
- Check for mechanical issues (belt tension, etc.)

### Issue: Slow to reach target speed
**Solution**:
- Increase kP (try 0.08-0.12)
- Increase kV (try 0.12-0.15)
- Consider if target speed is too high

## Testing Checklist

- [ ] Measure RPM at 28% power (baseline)
- [ ] Set target RPS = (measured RPM / 60)
- [ ] Start with kV = 0.10, adjust to match baseline
- [ ] Add kP = 0.05, tune for responsiveness
- [ ] Test consistency over 10+ shots
- [ ] Adjust for load (shooting game pieces)
- [ ] Fine-tune for competition conditions

## Pro Tips

1. **kV is most important** - Get this right first, then tune PID
2. **Start conservative** - Lower gains are safer than higher
3. **Test under load** - Empty motor ≠ loaded motor
4. **Use SysId** - WPILib's SysId tool gives you exact values
5. **Document changes** - Keep notes on what works

## Using SysId (Recommended)

The best way to get accurate values:
1. Run WPILib SysId tool
2. It will give you exact kS, kV, kA values
3. Use those values directly
4. Then tune PID for your specific needs

